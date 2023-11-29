use std::fmt::Display;

#[derive(Debug)]

pub struct ImmediateOutput<T: Display + Send> {
  inner: T,
}

impl<T: Display + Send> Display for ImmediateOutput<T> {
  fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
    write!(f, "{}", self.inner)
  }
}

impl<T: Display + Send> ImmediateOutput<T> {
  pub fn new(output: T) -> Self {
    Self { inner: output }
  }
}
