use std::sync::Arc;

use anyhow::anyhow;
use async_trait::async_trait;
use derive_builder::Builder;
use llm_sdk::{
  AssistantMessage, ChatCompleteModel, ChatCompletionMessage, ChatCompletionRequestBuilder, LlmSdk,
};
use secrecy::{ExposeSecret, Secret};
use tracing::info;

use crate::{
  client::Client,
  error::{ClientCreationError, ExecutorError},
  model::{ChatMLMessage, ChatRole, Output},
  options::{BuiltinOptions, Options},
};

pub struct OpenAIClient {
  client: Arc<LlmSdk>,
  options: Options<OpenAIOptions>,
}

pub const OPENAI_API_BASE: &str = "https://api.openai.com/v1";

#[derive(Builder)]
#[builder(default)]
pub struct OpenAIOptions {
  api_base: String,
  api_key: Option<Secret<String>>,
  max_http_retries: u32,
  model: ChatCompleteModel,
}

impl Default for OpenAIOptions {
  fn default() -> Self {
    OpenAIOptions {
      api_base: std::env::var("OPENAI_API_BASE")
        .unwrap_or_else(|_| OPENAI_API_BASE.to_string())
        .into(),
      api_key: std::env::var("OPENAI_API_KEY").map(|x| x.into()).ok(),
      max_http_retries: 3,
      model: ChatCompleteModel::Gpt3Turbo,
    }
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

  fn new_with_options(options: Options<Self::Opt<'_>>) -> Result<Self, ClientCreationError> {
    let api_key = options
      .custom
      .api_key
      .clone()
      .ok_or(ClientCreationError::FieldRequiredError(anyhow!(
        "OpenAI ApiKey is empty"
      )))?;
    let sdk = LlmSdk::new(
      options.custom.api_base.clone(),
      api_key.expose_secret(),
      options.custom.max_http_retries,
    );

    let client = Arc::new(sdk);
    Ok(Self { client, options })
  }

  async fn chat_complete(
    &self,
    messages: Vec<ChatMLMessage>,
    opt: BuiltinOptions,
  ) -> Result<Output, ExecutorError> {
    let mut request_builder = ChatCompletionRequestBuilder::default();

    if let Some(temperature) = opt.temperature {
      request_builder.temperature(temperature);
    };

    if let Some(top_p) = opt.top_p {
      request_builder.top_p(top_p);
    };

    let request = request_builder
      .messages(messages.into_iter().map(|x| x.into()).collect::<Vec<_>>())
      .model(self.options.custom.model)
      .build()
      .map_err(|err| anyhow!(err.to_string()))?;

    info!("request {:?}", request);

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
    let opt = OpenAIOptions::default();
    assert!(opt.api_key.is_none());

    env::set_var("OPENAI_API_KEY", "yes");

    let opt = OpenAIOptions::default();
    assert!(opt.api_key.is_some());
  }
}
