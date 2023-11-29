use std::io::Write;

use clap::Parser;
use flappy_llm::{
  client::Client,
  local::client::{LocalClient, LocalOptionsBuilder},
  options::BuiltinOptionsBuilder,
  prompt::{ChatMLMessage, Prompt},
};
use hf_hub::api::tokio::ApiBuilder;
use tokio_stream::StreamExt;
use tracing::info;

#[derive(Parser, Debug)]
#[command(author, version, about, long_about = None)]
struct Args {
  /// User Prompt
  #[arg(short, long, default_value = "who are you")]
  prompt: String,

  /// RUST_LOG environment
  #[arg(short, long, default_value = "INFO")]
  log_leval: String,

  /// STREAM
  #[arg(short, long, default_value_t = true)]
  stream: bool,
}

#[tokio::main(flavor = "current_thread")]
async fn main() {
  let args = Args::parse();

  std::env::set_var("RUST_LOG", args.log_leval);
  tracing_subscriber::fmt::init();

  let api = ApiBuilder::new().with_progress(true).build().unwrap();
  let repo = api.model("TheBloke/Flan-OpenLlama-7B-GGML".to_string());
  let filename = repo.get("flan-openllama-7b.ggmlv3.q2_K.bin").await.unwrap();

  let user_prompt = args.prompt;
  let prompt = Prompt::new_chat(vec![ChatMLMessage::new_user(user_prompt)]);
  let options = BuiltinOptionsBuilder::default()
    .top_k(Some(35.0))
    .top_p(Some(0.9))
    .temperature(Some(0.7))
    .build()
    .unwrap();

  let local_options = LocalOptionsBuilder::default()
    .model_path(Some(filename))
    .build()
    .unwrap();
  let client = LocalClient::new_with_custom_options(local_options).unwrap();

  if args.stream {
    let mut stream = client.chat_complete_stream(prompt, options).await.unwrap();

    while let Some(v) = stream.next().await {
      print!("{}", v);
      std::io::stdout().flush().unwrap();
    }
  } else {
    let result = client.chat_complete(prompt, options).await.unwrap();
    info!("result {}", result);
  }
}
