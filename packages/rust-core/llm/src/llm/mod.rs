use async_trait::async_trait;

use crate::error::Error;

#[cfg(feature = "openai")]
pub mod openai;

pub enum ChatRole {
  System,
  User,
  Assistant,
}

#[allow(dead_code)]
pub struct ChatMLMessage {
  role: ChatRole,
  content: String,
}

#[allow(dead_code)]
pub struct GenerateConfig {
  max_tokens: Option<f64>,
  temperature: Option<f64>,
  top_p: Option<f64>,
}

pub type LLMResult = String;

#[async_trait]
pub trait LLM {
  async fn chat_complete(
    &self,
    messages: Vec<ChatMLMessage>,
    config: Option<GenerateConfig>,
  ) -> Result<LLMResult, Error>;
}
