use std::fmt::Display;

use anyhow::anyhow;

use crate::{
  error::ExecuteError,
  output::stream::Streamable,
  prompt::{ChatMLMessage, ChatRole},
};

#[derive(Clone, Debug)]
pub enum OpenAIStreamFragment {
  Role(ChatRole),
  Delta(String),
}

impl Streamable for OpenAIStreamFragment {
  type Output = ChatMLMessage;

  fn join_segments(segments: Vec<Self>) -> Result<Self::Output, ExecuteError> {
    let mut default_role = ChatRole::Assistant;
    let mut messages = vec![];

    for segment in segments {
      match segment {
        OpenAIStreamFragment::Role(role) => default_role = role,
        OpenAIStreamFragment::Delta(delta) => messages.push(delta),
      }
    }

    if messages.is_empty() {
      return Err(ExecuteError::from(anyhow!("messages is blank")));
    }

    Ok(ChatMLMessage::new(default_role, messages.join("")))
  }
}

impl Display for OpenAIStreamFragment {
  fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
    match self {
      OpenAIStreamFragment::Role(role) => write!(f, "[{}]", role),
      OpenAIStreamFragment::Delta(delta) => write!(f, "{}", delta),
    }
  }
}
