use flappy_llm::{client::Client, model::ChatMLMessage, openai::client::OpenAIClient};
use tracing::info;

#[tokio::main]
async fn main() {
  tracing_subscriber::fmt::init();
  let client = OpenAIClient::new().unwrap();

  let result = client
    .chat_complete(
      [ChatMLMessage::user("hello".to_string())].to_vec(),
      Default::default(),
    )
    .await
    .unwrap();

  info!("result {:?}", result);
}
