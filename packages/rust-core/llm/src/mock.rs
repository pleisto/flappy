use async_trait::async_trait;

use crate::{
  client::Client,
  error::{ClientCreationError, ExecuteError},
  options::{BuiltinOptions, Options},
  output::{stream::StreamItem, ImmediateOutput, StreamOutput},
  prompt::Prompt,
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
  type Output<'a> = String;
  type StreamSegment<'a> = String;

  fn new_with_options(options: Options<Self::Opt<'_>>) -> Result<Self, ClientCreationError> {
    Ok(Self { options })
  }

  async fn chat_complete(
    &self,
    prompt: Prompt,
    _: BuiltinOptions,
  ) -> Result<ImmediateOutput<String>, ExecuteError> {
    let data = prompt.to_string();
    Ok(ImmediateOutput::new(data))
  }

  async fn chat_complete_stream(
    &self,
    prompt: Prompt,
    _: BuiltinOptions,
  ) -> Result<StreamOutput<String>, ExecuteError> {
    let (sender, output) = StreamOutput::<String>::new();

    let _ = prompt
      .to_messages()
      .into_iter()
      .map(|msg| sender.send(StreamItem::Data(msg.content)))
      .collect::<Result<Vec<_>, _>>()
      .map_err(|err| ExecuteError::Inner(err.into()))?;

    Ok(output)
  }
}

#[cfg(test)]
mod tests {

  use tokio_stream::StreamExt;

  use super::*;
  use crate::prompt::ChatMLMessage;

  #[tokio::test]
  async fn mock() {
    let client = MockClient::new().unwrap();

    let result = client
      .chat_complete(
        Prompt::new_chat(vec![ChatMLMessage::new_user("hello".to_string())]),
        Default::default(),
      )
      .await
      .unwrap();

    assert_eq!(result.to_string(), "User: hello\n");
  }

  #[tokio::test]
  async fn mock_stream_into() {
    let client = MockClient::new().unwrap();

    let mut result = client
      .chat_complete_stream(
        Prompt::new_chat(vec![
          ChatMLMessage::new_user("hello".to_string()),
          ChatMLMessage::new_user("world".to_string()),
        ]),
        Default::default(),
      )
      .await
      .unwrap();

    assert_eq!(
      result.to_immediate().await.unwrap().to_string(),
      "helloworld"
    );
  }

  #[tokio::test]
  async fn mock_stream_listen() {
    let client = MockClient::new().unwrap();

    let mut stream = client
      .chat_complete_stream(
        Prompt::new_chat(vec![
          ChatMLMessage::new_user("hello".to_string()),
          ChatMLMessage::new_user("world".to_string()),
        ]),
        Default::default(),
      )
      .await
      .unwrap();

    let mut output = String::new();

    while let Some(v) = stream.next().await {
      output.push_str(&v.to_string());
    }

    assert_eq!(output, "helloworld");
  }
}
