#[derive(Clone, Debug)]
pub enum ChatRole {
  System,
  User,
  Assistant,
}

#[derive(Clone, Debug)]
pub struct ChatMLMessage {
  pub(crate) role: ChatRole,
  pub(crate) content: String,
}

impl ChatMLMessage {
  pub fn user(content: String) -> Self {
    ChatMLMessage {
      role: ChatRole::User,
      content,
    }
  }

  pub fn assistant(content: String) -> Self {
    ChatMLMessage {
      role: ChatRole::Assistant,
      content,
    }
  }

  pub fn system(content: String) -> Self {
    ChatMLMessage {
      role: ChatRole::System,
      content,
    }
  }
}

#[derive(Debug)]
pub struct Output(pub String);
