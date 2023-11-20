use async_trait::async_trait;

use crate::{
  error::{ExecutorCreationError, ExecutorError},
  model::{ChatMLMessage, Output},
  options::{OptionInitial, Options},
};

#[async_trait]
pub trait Client: Sized {
  type Opt<'a>: OptionInitial
  where
    Self: 'a;

  fn new_with_options(options: Options<Self::Opt<'_>>) -> Result<Self, ExecutorCreationError>;

  fn new() -> Result<Self, ExecutorCreationError> {
    Self::new_with_options(Options::new())
  }

  async fn chat_complete(
    &self,
    messages: Vec<ChatMLMessage>,
    config: Options<Self::Opt<'_>>,
  ) -> Result<Output, ExecutorError>;
}
