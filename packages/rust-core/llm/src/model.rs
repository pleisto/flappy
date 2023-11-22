use std::{collections::VecDeque, fmt::Display};

use serde::Serialize;

#[derive(Clone, Debug, Serialize)]
pub enum ChatRole {
  System,
  User,
  Assistant,
}

impl Display for ChatRole {
  fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
    match self {
      ChatRole::User => write!(f, "User"),
      ChatRole::Assistant => write!(f, "Assistant"),
      ChatRole::System => write!(f, "System"),
    }
  }
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

impl Display for ChatMLMessage {
  fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
    write!(f, "{}: {}", self.role, self.content)
  }
}

pub struct Text(String);

impl Text {
  fn inner(&self) -> String {
    self.0.clone()
  }
}
pub struct Chat(VecDeque<ChatMLMessage>);

impl Chat {
  fn inner(&self) -> VecDeque<ChatMLMessage> {
    self.0.clone()
  }
}

impl Display for Chat {
  fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
    for message in self.0.iter() {
      writeln!(f, "{}", message)?;
    }
    Ok(())
  }
}

pub enum Prompt {
  Chat(Chat),
  Text(Text),
}

impl Prompt {
  pub fn new_chat(messages: Vec<ChatMLMessage>) -> Self {
    Self::Chat(Chat(messages.into()))
  }

  pub fn new_text(text: String) -> Self {
    Self::Text(Text(text))
  }

  pub fn to_messages(&self) -> Vec<ChatMLMessage> {
    match self {
      Prompt::Chat(chat) => chat.inner().into(),
      Prompt::Text(text) => vec![ChatMLMessage::user(text.inner())],
    }
  }
}

impl ToString for Prompt {
  fn to_string(&self) -> String {
    match self {
      Prompt::Chat(chat) => chat.to_string(),
      Prompt::Text(text) => text.inner().to_string(),
    }
  }
}

#[derive(Debug)]
pub struct Output(String);

impl Display for Output {
  fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
    write!(f, "{}", self.0)
  }
}

impl Output {
  pub fn new(output: String) -> Self {
    Self(output)
  }
}

#[cfg(test)]
mod tests {
  use super::*;

  #[test]
  fn text() {
    let text = Prompt::new_text("abc".to_string());

    assert_eq!(text.to_string(), "abc");
    assert_eq!(text.to_messages().len(), 1)
  }

  #[test]
  fn chat() {
    let chat1 = Prompt::new_chat(vec![]);
    assert_eq!(chat1.to_string(), "");
    assert_eq!(chat1.to_messages().len(), 0);

    let chat2 = Prompt::new_chat(vec![ChatMLMessage::user("foo".to_string())]);
    assert_eq!(chat2.to_string(), "User: foo\n");
    assert_eq!(chat2.to_messages().len(), 1)
  }
}
