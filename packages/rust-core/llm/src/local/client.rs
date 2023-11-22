use std::default;

use async_trait::async_trait;
use llm::{Model, ModelArchitecture};

use crate::{
  client::Client,
  error::{ClientCreationError, ExecutorError},
  model::{ChatMLMessage, Output},
  options::{BuiltinOptions, Options},
};

#[derive(Default)]
pub struct LocalOptions {
  model_arch: String,
}

pub struct LocalClient {
  model_client: Box<dyn Model>,
  options: Options<LocalOptions>,
}

#[async_trait]
impl Client for LocalClient {
  type Opt<'a> = LocalOptions;

  fn new_with_options(options: Options<Self::Opt<'_>>) -> Result<Self, ClientCreationError> {
    let model_arch = options
      .custom
      .model_arch
      .parse::<ModelArchitecture>()
      .map_err(|e| ClientCreationError::InnerError(Box::new(e)))?;

    let llm: Box<dyn Model> = llm::load(
      model_arch,
      Path::new(&model_path.to_path()),
      model_params_from_options(opts_cas).map_err(|_| {
        ExecutorCreationError::FieldRequiredError("Default field missing".to_string())
      })?,
      load_progress_callback_stdout,
    )
    .map_err(|e| ClientCreationError::InnerError(Box::new(e)))?;

    Ok(Self { options })
  }

  async fn chat_complete(
    &self,
    messages: Vec<ChatMLMessage>,
    _: BuiltinOptions,
  ) -> Result<Output, ExecutorError> {
    let data = serde_json::to_value(messages)?.to_string();
    Ok(Output(data))
  }
}
