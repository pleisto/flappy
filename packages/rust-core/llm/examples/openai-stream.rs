use std::io::Write;

use flappy_llm::{
  client::Client,
  openai::client::OpenAIClient,
  prompt::{ChatMLMessage, Prompt},
};
use futures::StreamExt;

#[tokio::main(flavor = "current_thread")]
async fn main() {
  tracing_subscriber::fmt::init();

  let client = OpenAIClient::new().unwrap();

  let mut stream = client
    .chat_complete_stream(
      Prompt::new_chat(vec![ChatMLMessage::new_user("hello".to_string())]),
      Default::default(),
    )
    .await
    .unwrap();

  while let Some(v) = stream.next().await {
    print!("{}", v);
    std::io::stdout().flush().unwrap();
  }

  // let result = stream.to_immediate().await.unwrap();
  // info!("result {:?}", result);
}
