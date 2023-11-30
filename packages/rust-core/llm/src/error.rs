use std::error::Error;

#[derive(thiserror::Error, Debug)]
#[error("unable to create client")]
pub enum ClientCreationError {
  #[error("unable to create client: {0}")]
  Inner(#[from] Box<dyn Error + Send + Sync>),

  #[error("Field must be set: {0}")]
  FieldRequired(anyhow::Error),
}

#[derive(thiserror::Error, Debug)]
pub enum ExecuteError {
  #[error("execute error: {0}")]
  Inner(#[from] Box<dyn Error + Send + Sync>),

  #[error("Unexpected error: {0}")]
  Anyhow(#[from] anyhow::Error),

  #[error("Serialize error: {0}")]
  Serialize(#[from] serde_json::Error),

  #[error("Invalid role: {0}")]
  InvalidRole(#[from] InvalidRoleError),
}

#[derive(thiserror::Error, Debug)]
#[error("Invalid role")]
pub struct InvalidRoleError(pub(crate) String);
