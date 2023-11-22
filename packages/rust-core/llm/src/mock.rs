use async_trait::async_trait;

use crate::{
  client::Client,
  error::{ClientCreationError, ExecutorError},
  model::{ChatMLMessage, Output},
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
    messages: Vec<ChatMLMessage>,
    _: BuiltinOptions,
  ) -> Result<Output, ExecutorError> {
    let data = serde_json::to_value(messages)?.to_string();
    Ok(Output(data))
  }
}

#[cfg(test)]
mod tests {

  use super::*;

  #[tokio::test]
  async fn mock() {
    let client = MockClient::new().unwrap();

    let result = client
      .chat_complete(
        vec![ChatMLMessage::user("hello".to_string())],
        BuiltinOptions::default(),
      )
      .await
      .unwrap();

    assert_eq!(result.0, "[{\"content\":\"hello\",\"role\":\"User\"}]");
  }
}
