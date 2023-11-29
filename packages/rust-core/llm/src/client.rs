use std::fmt::Display;

use async_trait::async_trait;

use crate::{
  error::{ClientCreationError, ExecuteError},
  options::{BuiltinOptions, Options},
  output::{stream::Streamable, ImmediateOutput, StreamOutput},
  prompt::Prompt,
};

#[async_trait]
pub trait Client: Sized {
  type Opt<'a>: Default
  where
    Self: 'a;
  type Output<'a>: Display + Send
  where
    Self: 'a;
  type StreamSegment<'a>: Display + Send + Streamable<Output = Self::Output<'a>>
  where
    Self: 'a;

  fn new_with_options(options: Options<Self::Opt<'_>>) -> Result<Self, ClientCreationError>;

  fn new_with_custom_options(custom: Self::Opt<'_>) -> Result<Self, ClientCreationError> {
    Self::new_with_options(Options::new(BuiltinOptions::default(), custom))
  }

  fn new() -> Result<Self, ClientCreationError> {
    Self::new_with_options(Default::default())
  }

  async fn chat_complete(
    &self,
    prompt: Prompt,
    config: BuiltinOptions,
  ) -> Result<ImmediateOutput<Self::Output<'_>>, ExecuteError>;

  async fn chat_complete_stream(
    &self,
    prompt: Prompt,
    config: BuiltinOptions,
  ) -> Result<StreamOutput<Self::StreamSegment<'_>>, ExecuteError>;
}
