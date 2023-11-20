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

pub struct Output(String);
