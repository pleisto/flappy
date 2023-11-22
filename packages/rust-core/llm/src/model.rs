use serde::Serialize;

#[derive(Clone, Debug, Serialize)]
pub enum ChatRole {
  System,
  User,
  Assistant,
}

#[derive(Clone, Debug, Serialize)]
pub struct ChatMLMessage {
  pub(crate) role: ChatRole,
  pub(crate) content: String,
}

impl ChatMLMessage {
  pub fn user(content: String) -> Self {
    Self {
      role: ChatRole::User,
      content,
    }
  }

  pub fn assistant(content: String) -> Self {
    Self {
      role: ChatRole::Assistant,
      content,
    }
  }

  pub fn system(content: String) -> Self {
    Self {
      role: ChatRole::System,
      content,
    }
  }
}

pub struct ChatMLMessages(Vec<ChatMLMessage>);

#[derive(Debug)]
pub struct Output(pub String);
