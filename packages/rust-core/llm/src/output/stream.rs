use futures::StreamExt;
use std::fmt::{self, Display};
use std::pin::Pin;
use std::task::{Context, Poll};
use tokio::sync::mpsc::{self, UnboundedReceiver};
use tokio_stream::Stream;

use crate::error::ExecuteError;

use super::ImmediateOutput;

#[derive(Debug)]
pub enum StreamItem<T: Display + Send + Streamable> {
  Data(T),
  Err(ExecuteError),
}

impl<T: Display + Send + Streamable> fmt::Display for StreamItem<T> {
  fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
    match self {
      StreamItem::Data(content) => write!(f, "{}", content),
      StreamItem::Err(executor_error) => write!(f, "{}", executor_error),
    }
  }
}

#[derive(Debug)]
pub struct StreamOutput<T: Display + Send + Streamable> {
  receiver: UnboundedReceiver<StreamItem<T>>,
}

impl<T: Display + Send + Streamable> Display for StreamOutput<T> {
  fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
    write!(f, "[Stream]")
  }
}

impl<T: Display + Send + Streamable + 'static> StreamOutput<T> {
  pub fn new() -> (mpsc::UnboundedSender<StreamItem<T>>, Self) {
    let (sender, receiver) = mpsc::unbounded_channel();
    (sender, Self { receiver })
  }

  pub fn from_stream<S>(stream: S) -> Self
  where
    S: Stream<Item = StreamItem<T>> + Send + 'static,
  {
    let (sender, receiver) = mpsc::unbounded_channel();
    let sender_clone = sender;
    let mut stream = Box::pin(stream);

    tokio::spawn(async move {
      while let Some(item) = stream.next().await {
        if sender_clone.send(item).is_err() {
          break;
        }
      }
    });

    Self { receiver }
  }

  pub async fn to_immediate<Output: Display + Send>(
    &mut self,
  ) -> Result<ImmediateOutput<Output>, ExecuteError>
  where
    T: Display + Send + Streamable<Output = Output>,
  {
    let mut segments = vec![];

    while let Some(segment) = self.receiver.recv().await {
      match segment {
        StreamItem::Data(data) => segments.push(data),
        StreamItem::Err(err) => return Err(err),
      }
    }

    let output = T::join_segments(segments)?;
    Ok(ImmediateOutput::new(output))
  }
}

impl<T: Display + Send + Streamable> Stream for StreamOutput<T> {
  type Item = StreamItem<T>;

  fn poll_next(mut self: Pin<&mut Self>, cx: &mut Context<'_>) -> Poll<Option<Self::Item>> {
    self.receiver.poll_recv(cx)
  }
}

pub trait Streamable: Sized {
  type Output: Display + Send;

  fn join_segments(segments: Vec<Self>) -> Result<Self::Output, ExecuteError>;
}

impl Streamable for String {
  type Output = String;

  fn join_segments(segments: Vec<Self>) -> Result<Self::Output, ExecuteError> {
    Ok(segments.join(""))
  }
}
