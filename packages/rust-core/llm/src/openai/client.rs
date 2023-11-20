use std::sync::Arc;

use async_openai::config::OpenAIConfig;
use async_trait::async_trait;
use strum_macros::EnumDiscriminants;

use crate::{
  client::Client,
  error::{ExecutorCreationError, ExecutorError},
  model::{ChatMLMessage, Output},
  options::{OptionInitial, Options},
};

pub struct OpenAIClient {
  client: Arc<async_openai::Client<OpenAIConfig>>,
  options: Options<OpenAIOptions>,
}

#[derive(EnumDiscriminants)]
#[strum_discriminants(vis(pub(crate)))]
pub enum OpenAIOptions {
  ApiBase(String),
  APIKey(String),
  OrgId(String),
}

impl OptionInitial for OpenAIOptions {}

#[async_trait]
impl Client for OpenAIClient {
  type Opt<'a> = OpenAIOptions;

  fn new() -> Result<Self, ExecutorCreationError> {
    Self::new_with_options(Options::new())
  }

  fn new_with_options(options: Options<Self::Opt<'_>>) -> Result<Self, ExecutorCreationError> {
    let mut cfg = OpenAIConfig::new();

    if let Some(OpenAIOptions::APIKey(api_key)) = options.get_custom(|item| {
      OpenAIOptionsDiscriminants::from(item) == OpenAIOptionsDiscriminants::APIKey
    }) {
      cfg = cfg.with_api_key(api_key)
    }

    if let Some(OpenAIOptions::ApiBase(api_base)) = options.get_custom(|item| {
      OpenAIOptionsDiscriminants::from(item) == OpenAIOptionsDiscriminants::ApiBase
    }) {
      cfg = cfg.with_api_base(api_base)
    }

    if let Some(OpenAIOptions::OrgId(org_id)) = options.get_custom(|item| {
      OpenAIOptionsDiscriminants::from(item) == OpenAIOptionsDiscriminants::OrgId
    }) {
      cfg = cfg.with_org_id(org_id)
    }

    let client = Arc::new(async_openai::Client::with_config(cfg));
    Ok(Self { client, options })
  }

  async fn chat_complete(
    &self,
    messages: Vec<ChatMLMessage>,
    config: Options<Self::Opt<'_>>,
  ) -> Result<Output, ExecutorError> {
    panic!("123")
  }
}
