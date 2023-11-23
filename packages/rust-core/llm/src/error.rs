use std::error::Error;

#[derive(thiserror::Error, Debug)]
#[error("unable to create client")]
pub enum ClientCreationError {
  #[error("unable to create client: {0}")]
  InnerError(#[from] Box<dyn Error + Send + Sync>),

  #[error("Field must be set: {0}")]
  FieldRequiredError(anyhow::Error),
}

#[derive(thiserror::Error, Debug)]
pub enum ExecutorError {
  #[error("execute error: {0}")]
  InnerError(#[from] Box<dyn Error + Send + Sync>),

  #[error("Unexpected error: {0}")]
  Anyhow(#[from] anyhow::Error),

  #[error("Serialize error: {0}")]
  SerializeError(#[from] serde_json::Error),
}
