use flappy_llm::{
  client::Client,
  local::client::{LocalClient, LocalOptionsBuilder},
  model::{ChatMLMessage, Prompt},
  options::BuiltinOptionsBuilder,
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

  let local_options = LocalOptionsBuilder::default()
    .model_path(Some(filename))
    .build()
    .unwrap();
  let client = LocalClient::new_with_custom_options(local_options).unwrap();

  let result = client
    .chat_complete(
      Prompt::new_chat(vec![ChatMLMessage::user("who are you".to_string())]),
      BuiltinOptionsBuilder::default()
        .top_k(Some(35.0))
        .top_p(Some(0.9))
        .temperature(Some(0.7))
        .build()
        .unwrap(),
    )
    .await
    .unwrap();

  info!("result {}", result);
}
