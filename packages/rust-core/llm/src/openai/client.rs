use std::sync::Arc;

use anyhow::anyhow;
use async_openai::{
  config::OpenAIConfig,
  error::OpenAIError,
  types::{
    ChatCompletionRequestAssistantMessageArgs, ChatCompletionRequestMessage,
    ChatCompletionRequestSystemMessageArgs, ChatCompletionRequestUserMessageArgs,
    CreateChatCompletionRequest, CreateChatCompletionRequestArgs, Role,
  },
};
use async_trait::async_trait;
use futures::StreamExt;
use secrecy::ExposeSecret;
use tracing::{debug, info};

use crate::{
  client::Client,
  error::{ClientCreationError, ExecuteError},
  options::{BuiltinOptions, Options},
  output::{stream::StreamItem, ImmediateOutput, StreamOutput},
  prompt::{ChatMLMessage, ChatRole, Prompt},
};

use super::{options::OpenAIOptions, stream::OpenAIStreamFragment};

pub struct OpenAIClient {
  client: Arc<async_openai::Client<OpenAIConfig>>,
  options: Options<OpenAIOptions>,
}

impl From<Role> for ChatRole {
  fn from(value: Role) -> Self {
    match value {
      Role::Assistant => ChatRole::Assistant,
      Role::System => ChatRole::System,
      Role::User => ChatRole::User,
      _ => ChatRole::Assistant,
    }
  }
}

impl OpenAIClient {
  fn convert_message(x: &ChatMLMessage) -> Result<ChatCompletionRequestMessage, OpenAIError> {
    Ok(match x.role {
      ChatRole::Assistant => ChatCompletionRequestMessage::Assistant(
        ChatCompletionRequestAssistantMessageArgs::default()
          .content(x.content.clone())
          .build()?,
      ),
      ChatRole::System => ChatCompletionRequestMessage::System(
        ChatCompletionRequestSystemMessageArgs::default()
          .content(x.content.clone())
          .build()?,
      ),
      ChatRole::User => ChatCompletionRequestMessage::User(
        ChatCompletionRequestUserMessageArgs::default()
          .content(x.content.clone())
          .build()?,
      ),
    })
  }

  fn build_request(
    &self,
    prompt: Prompt,
    opt: BuiltinOptions,
    stream: bool,
  ) -> Result<CreateChatCompletionRequest, ExecuteError> {
    let messages = prompt
      .to_messages()
      .iter()
      .map(OpenAIClient::convert_message)
      .collect::<Result<Vec<_>, _>>()
      .map_err(|err| ExecuteError::Inner(err.into()))?;

    let mut builder = CreateChatCompletionRequestArgs::default();

    if let Some(temperature) = opt.temperature {
      builder.temperature(temperature);
    };

    if let Some(top_p) = opt.top_p {
      builder.top_p(top_p);
    };

    builder
      .model(self.options.custom.model.clone())
      .stream(stream)
      .messages(messages)
      .build()
      .map_err(|err| ExecuteError::Inner(err.into()))
  }
}

#[async_trait]
impl Client for OpenAIClient {
  type Opt<'a> = OpenAIOptions;
  type Output<'a> = ChatMLMessage;
  type StreamSegment<'a> = OpenAIStreamFragment;

  fn new_with_options(options: Options<Self::Opt<'_>>) -> Result<Self, ClientCreationError> {
    let api_key = options
      .custom
      .api_key
      .clone()
      .ok_or(ClientCreationError::FieldRequired(anyhow!(
        "OpenAI ApiKey is empty"
      )))?;

    let client_options = OpenAIConfig::new()
      .with_api_base(options.custom.api_base.clone())
      .with_api_key(api_key.expose_secret())
      .with_org_id(options.custom.org_id.clone());

    let http_client = reqwest::Client::new();
    let client = async_openai::Client::with_config(client_options)
      .with_http_client(http_client)
      .with_backoff(backoff::ExponentialBackoff::default());

    Ok(Self {
      options,
      client: Arc::new(client),
    })
  }

  async fn chat_complete(
    &self,
    prompt: Prompt,
    opt: BuiltinOptions,
  ) -> Result<ImmediateOutput<ChatMLMessage>, ExecuteError> {
    let request = self.build_request(prompt, opt, false)?;

    info!("request {:?}", request);

    let result = self
      .client
      .chat()
      .create(request)
      .await
      .map_err(|err| ExecuteError::Inner(err.into()))?;

    info!("response {:?}", result);

    let first_choice = result.choices.first().ok_or(anyhow!("Choices is empty"))?;
    let content_message = &first_choice.message;
    let optional_content = content_message.content.clone();
    let content = optional_content.ok_or(anyhow!("Content is empty"))?;
    let message = ChatMLMessage::new(content_message.role.into(), content);

    Ok(ImmediateOutput::new(message))
  }

  async fn chat_complete_stream(
    &self,
    prompt: Prompt,
    opt: BuiltinOptions,
  ) -> Result<StreamOutput<OpenAIStreamFragment>, ExecuteError> {
    let request = self.build_request(prompt, opt, true)?;

    info!("request {:?}", request);

    let resp = self
      .client
      .chat()
      .create_stream(request)
      .await
      .map_err(|e| ExecuteError::Inner(e.into()))?;

    let stream = resp.flat_map(|result| {
      let mut v: Vec<StreamItem<OpenAIStreamFragment>> = vec![];

      let optional_item: Option<StreamItem<OpenAIStreamFragment>> = match result {
        Err(err) => Some(StreamItem::Err(ExecuteError::Inner(err.into()))),
        Ok(resp) => match resp.choices.first() {
          None => Some(StreamItem::Err(ExecuteError::Anyhow(anyhow!(
            "Choices is empty"
          )))),
          Some(choice) => {
            let delta = choice.delta.clone();
            debug!("delta {:?}", delta);

            if let Some(content) = delta.content {
              Some(StreamItem::Data(OpenAIStreamFragment::Delta(content)))
            } else {
              delta
                .role
                .map(|role| StreamItem::Data(OpenAIStreamFragment::Role(role.into())))
            }
          }
        },
      };

      if let Some(item) = optional_item {
        v.push(item)
      }

      futures::stream::iter(v)
    });

    Ok(StreamOutput::from_stream(stream))
  }
}

#[cfg(test)]
mod tests {
  use std::env;

  use super::*;

  #[test]
  fn options() {
    let opt = OpenAIOptions::default();
    assert!(opt.api_key.is_none());

    env::set_var("OPENAI_API_KEY", "yes");

    let opt = OpenAIOptions::default();
    assert!(opt.api_key.is_some());
  }
}
