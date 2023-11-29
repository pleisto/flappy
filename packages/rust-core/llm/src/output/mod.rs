pub mod immediate;
pub mod stream;

use std::fmt::Display;

pub use immediate::ImmediateOutput;
pub use stream::StreamOutput;

use self::stream::Streamable;

pub enum Output<T: Display + Send + Streamable> {
  Immediate(ImmediateOutput<T>),
  Stream(StreamOutput<T>),
}

impl<T: Display + Send + Streamable> Display for Output<T> {
  fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
    match self {
      Output::Immediate(inner) => write!(f, "{}", inner),
      Output::Stream(inner) => write!(f, "{}", inner),
    }
  }
}
