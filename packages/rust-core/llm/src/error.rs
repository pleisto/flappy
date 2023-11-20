use thiserror::Error;

#[derive(thiserror::Error, Debug)]
#[error("unable to create executor")]
pub enum ExecutorCreationError {
  #[error("unable to create executor: {0}")]
  InnerError(#[from] Box<dyn std::error::Error + Send + Sync>),
  #[error("Field must be set: {0}")]
  FieldRequiredError(String),
}

#[derive(thiserror::Error, Debug)]
pub enum ExecutorError {
  #[error("Unable to run model: {0}")]
  /// An error occuring in the underlying executor code that doesn't fit any other category.
  InnerError(#[from] Box<dyn std::error::Error + Send + Sync>),
  #[error("Invalid options when calling the executor")]
  /// An error indicating that the model was invoked with invalid options
  InvalidOptions,
  #[error(transparent)]
  /// An error tokenizing the prompt.
  PromptTokens(PromptTokensError),
  #[error("the context was to small to fit your input")]
  ContextTooSmall,
}

#[derive(Error, Debug, Clone)]
pub enum TokenizerError {
  #[error("Error tokenizing input text")]
  TokenizationError,
  #[error("Error stringifying tokens to text")]
  ToStringError,
  #[error("Error creating tokenizer")]
  TokenizerCreationError,
  #[error("Token Collection type mismatch")]
  TokenCollectionTypeMismatch,
}

#[derive(Clone, Debug, Error)]
pub enum PromptTokensError {
  /// Indicates that prompt tokens are not accessible for the given step.
  #[error("The prompt tokens are not accessible for this type of step.")]
  NotAvailable,
  /// Indicates that the prompt tokens could not be computed.
  #[error("The prompt tokens could not be computed.")]
  UnableToCompute,
  /// Indicates that the prompt tokens could not be computed because formatting the prompt failed.
  #[error("Tokenizer error: {0}")]
  TokenizerError(#[from] TokenizerError),
}

#[derive(Error, Debug)]
pub enum Error {
  #[error("IO error: {0}")]
  IO(#[from] std::io::Error),
}
