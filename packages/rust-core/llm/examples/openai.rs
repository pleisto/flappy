use flappy_llm::{
  client::Client,
  openai::client::OpenAIClient,
  prompt::{ChatMLMessage, Prompt},
};
use tracing::info;

#[tokio::main(flavor = "current_thread")]
async fn main() {
  tracing_subscriber::fmt::init();

  let client = OpenAIClient::new().unwrap();

  let result = client
    .chat_complete(
      Prompt::new_chat(vec![ChatMLMessage::new_user("hello".to_string())]),
      Default::default(),
    )
    .await
    .unwrap();

  info!("result {}", result);
}
