use flappy_llm::{
  client::Client, model::ChatMLMessage, openai::client::OpenAIClient, options::BuiltinOptions,
};
use tracing::info;

#[tokio::main]
async fn main() {
  tracing_subscriber::fmt::init();
  let client = OpenAIClient::new().unwrap();

  let result = client
    .chat_complete(
      [ChatMLMessage::user("hello".to_string())].to_vec(),
      BuiltinOptions::default(),
    )
    .await
    .unwrap();

  info!("result {:?}", result);
}
