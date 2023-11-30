use std::io::Write;

use clap::Parser;
use flappy_llm::{
  client::Client,
  openai::client::OpenAIClient,
  options::BuiltinOptionsBuilder,
  prompt::{ChatMLMessage, Prompt},
};
use tokio_stream::StreamExt;
use tracing::info;

#[derive(Parser, Debug)]
#[command(author, version, about, long_about = None)]
struct Args {
  /// User Prompt
  #[arg(short, long, default_value = "how to use trait in rust")]
  prompt: String,

  /// RUST_LOG environment
  #[arg(short, long, default_value = "INFO")]
  log_leval: String,

  /// STREAM
  #[arg(short, long, action)]
  no_stream: bool,
}

#[tokio::main(flavor = "current_thread")]
async fn main() {
  let args = Args::parse();

  tracing_subscriber::fmt::init();
  info!("args {:?}", args);
  std::env::set_var("RUST_LOG", args.log_leval);

  let client = OpenAIClient::new().unwrap();
  let user_prompt = args.prompt;
  let prompt = Prompt::new_chat(vec![ChatMLMessage::new_user(user_prompt)]);
  let options = BuiltinOptionsBuilder::default()
    .top_k(Some(35.0))
    .top_p(Some(0.9))
    .temperature(Some(0.7))
    .build()
    .unwrap();

  if args.no_stream {
    let result = client.chat_complete(prompt, options).await.unwrap();
    info!("result {}", result);
  } else {
    let mut stream = client.chat_complete_stream(prompt, options).await.unwrap();

    while let Some(v) = stream.next().await {
      print!("\x1b[31m{v}\x1b[0m");
      std::io::stdout().flush().unwrap();
    }
  }
}
