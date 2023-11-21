use std::sync::Arc;

use anyhow::anyhow;
use async_trait::async_trait;
use llm_sdk::{
  AssistantMessage, ChatCompleteModel, ChatCompletionMessage, ChatCompletionRequestBuilder, LlmSdk,
};
use secrecy::{ExposeSecret, Secret};
use strum_macros::EnumDiscriminants;
use tracing::info;

use crate::{
  client::Client,
  error::{ExecutorCreationError, ExecutorError},
  model::{ChatMLMessage, ChatRole, Output},
  options::{BuiltinOption, BuiltinOptionDiscriminants, BuiltinOptions, OptionInitial, Options},
};

pub struct OpenAIClient {
  client: Arc<LlmSdk>,
  options: Options<OpenAIOptions>,
}

pub const OPENAI_API_BASE: &str = "https://api.openai.com/v1";

lazy_static::lazy_static! {}

#[derive(EnumDiscriminants, Clone)]
#[strum_discriminants(vis(pub(crate)))]
pub enum OpenAIOptions {
  ApiBase(String),
  APIKey(Secret<String>),
  MaxHTTPRetries(u32),
  Model(ChatCompleteModel),
}

impl OptionInitial for OpenAIOptions {
  fn initialize() -> Vec<Self> {
    let api_key_option = if let Ok(api_key) = std::env::var("OPENAI_API_KEY") {
      [OpenAIOptions::APIKey(api_key.into())].to_vec()
    } else {
      [].to_vec()
    };
    [
      &[
        OpenAIOptions::ApiBase(
          std::env::var("OPENAI_API_BASE")
            .unwrap_or_else(|_| OPENAI_API_BASE.to_string())
            .into(),
        ),
        OpenAIOptions::MaxHTTPRetries(3),
        OpenAIOptions::Model(ChatCompleteModel::Gpt3Turbo),
      ],
      &api_key_option[..],
    ]
    .concat()
  }
}

impl Into<ChatCompletionMessage> for ChatMLMessage {
  fn into(self) -> ChatCompletionMessage {
    let name = "";
    match self.role {
      ChatRole::Assistant => ChatCompletionMessage::Assistant(AssistantMessage {
        content: Some(self.content),
        name: None,
        tool_calls: Vec::new(),
      }),
      ChatRole::System => ChatCompletionMessage::new_system(self.content, name),
      ChatRole::User => ChatCompletionMessage::new_user(self.content, name),
    }
  }
}

#[async_trait]
impl Client for OpenAIClient {
  type Opt<'a> = OpenAIOptions;

  fn new() -> Result<Self, ExecutorCreationError> {
    Self::new_with_options(Options::new())
  }

  fn new_with_options(options: Options<Self::Opt<'_>>) -> Result<Self, ExecutorCreationError> {
    if let [Some(OpenAIOptions::APIKey(api_key)), Some(OpenAIOptions::ApiBase(api_base)), Some(OpenAIOptions::MaxHTTPRetries(max_retries))] =
      options.get_customs(vec![
        (|item: &&OpenAIOptions| {
          OpenAIOptionsDiscriminants::from(*item) == OpenAIOptionsDiscriminants::APIKey
        }),
        (|item: &&OpenAIOptions| {
          OpenAIOptionsDiscriminants::from(*item) == OpenAIOptionsDiscriminants::ApiBase
        }),
        (|item: &&OpenAIOptions| {
          OpenAIOptionsDiscriminants::from(*item) == OpenAIOptionsDiscriminants::MaxHTTPRetries
        }),
      ])[..]
    {
      let sdk = LlmSdk::new(api_base, api_key.expose_secret(), *max_retries);
      let client = Arc::new(sdk);
      Ok(Self { client, options })
    } else {
      return Err(ExecutorCreationError::FieldRequiredError(anyhow!(
        "Invalid OpenAI options"
      )));
    }
  }

  async fn chat_complete(
    &self,
    messages: Vec<ChatMLMessage>,
    opt: BuiltinOptions,
  ) -> Result<Output, ExecutorError> {
    let mut request_builder = ChatCompletionRequestBuilder::default();

    if let Some(BuiltinOption::Temperature(temperature)) =
      opt.get(BuiltinOptionDiscriminants::Temperature)
    {
      request_builder.temperature(*temperature);
    };

    if let Some(BuiltinOption::TopP(top_p)) = opt.get(BuiltinOptionDiscriminants::TopP) {
      request_builder.temperature(*top_p);
    };

    if let Some(OpenAIOptions::Model(model)) = self.options.get_custom(|item: &&OpenAIOptions| {
      OpenAIOptionsDiscriminants::from(*item) == OpenAIOptionsDiscriminants::Model
    }) {
      request_builder.model(*model);
    }

    info!("message {:?}", messages);

    let request = request_builder
      .messages(messages.into_iter().map(|x| x.into()).collect::<Vec<_>>())
      .build()
      .map_err(|err| anyhow!(err.to_string()))?;

    let result = self.client.chat_completion(request).await?;

    info!("request {:?}", result);

    let first_choice = result.choices.first().ok_or(anyhow!("Choices is empty"))?;
    let optional_content = first_choice.message.content.clone();
    let content = optional_content.ok_or(anyhow!("Content is empty"))?;

    Ok(Output(content))
  }
}

#[cfg(test)]
mod tests {

  use std::env;

  use super::*;

  #[test]
  fn options() {
    let options = Options::<OpenAIOptions>::new();
    assert_eq!(options.size(), 3);

    env::set_var("OPENAI_API_KEY", "yes");

    let options = Options::<OpenAIOptions>::new();
    assert_eq!(options.size(), 4);
  }
}
