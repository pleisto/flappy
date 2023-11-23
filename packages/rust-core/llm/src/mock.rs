use async_trait::async_trait;

use crate::{
  client::Client,
  error::{ClientCreationError, ExecutorError},
  model::{Output, Prompt},
  options::{BuiltinOptions, Options},
};

#[derive(Default)]
pub struct MockOptions {}

pub struct MockClient {
  #[allow(dead_code)]
  options: Options<MockOptions>,
}

#[async_trait]
impl Client for MockClient {
  type Opt<'a> = MockOptions;

  fn new_with_options(options: Options<Self::Opt<'_>>) -> Result<Self, ClientCreationError> {
    Ok(Self { options })
  }

  async fn chat_complete(
    &self,
    prompt: Prompt,
    _: BuiltinOptions,
  ) -> Result<Output, ExecutorError> {
    let data = prompt.to_string();
    Ok(Output::new(data))
  }
}

#[cfg(test)]
mod tests {

  use super::*;
  use crate::model::ChatMLMessage;

  #[tokio::test]
  async fn mock() {
    let client = MockClient::new().unwrap();

    let result = client
      .chat_complete(
        Prompt::new_chat(vec![ChatMLMessage::user("hello".to_string())]),
        Default::default(),
      )
      .await
      .unwrap();

    assert_eq!(result.to_string(), "User: hello\n");
  }
}
