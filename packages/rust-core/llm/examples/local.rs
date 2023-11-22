use flappy_llm::{
  client::Client,
  local::client::{LocalClient, LocalOptionsBuilder},
  model::{ChatMLMessage, Prompt},
};
use hf_hub::api::tokio::Api;
use tracing::info;

#[tokio::main(flavor = "current_thread")]
async fn main() {
  std::env::set_var("RUST_LOG", "DEBUG");
  tracing_subscriber::fmt::init();

  let api = Api::new().unwrap();
  let repo = api.model("rustformers/open-llama-ggml".to_string());
  let filename = repo.get("open_llama_3b-f16.bin").await.unwrap();

  let options = LocalOptionsBuilder::default()
    .model_arch(Some("llama".to_string()))
    .model_path(Some(filename))
    .build()
    .unwrap();
  let client = LocalClient::new_with_custom_options(options).unwrap();

  let result = client
    .chat_complete(
      Prompt::new_chat(vec![ChatMLMessage::user("hello".to_string())]),
      Default::default(),
    )
    .await
    .unwrap();

  info!("result {}", result);
}
