use derive_builder::Builder;
use secrecy::Secret;

pub const OPENAI_API_BASE: &str = "https://api.openai.com/v1";

#[derive(Builder)]
#[builder(default)]
pub struct OpenAIOptions {
  pub(crate) api_base: String,
  pub(crate) api_key: Option<Secret<String>>,
  pub(crate) org_id: String,
  pub(crate) model: String,
}

impl Default for OpenAIOptions {
  fn default() -> Self {
    OpenAIOptions {
      api_base: std::env::var("OPENAI_API_BASE").unwrap_or_else(|_| OPENAI_API_BASE.to_string()),
      api_key: std::env::var("OPENAI_API_KEY").map(|x| x.into()).ok(),
      org_id: Default::default(),
      model: "gpt-3.5-turbo".to_string(),
    }
  }
}
