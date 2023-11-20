#[allow(dead_code)]
pub enum Model {
  GPT3Turbo,
  GPT4,
  Other(String),
}

impl ToString for Model {
  fn to_string(&self) -> String {
    match &self {
      Model::GPT3Turbo => "gpt-3.5-turbo".to_string(),
      Model::GPT4 => "gpt-4".to_string(),
      Model::Other(model) => model.to_string(),
    }
  }
}
