use std::fmt::Debug;
use std::fmt::Display;
use std::fmt::Formatter;

use jni::objects::JThrowable;
use jni::objects::JValue;
use jni::JNIEnv;
use std::backtrace::Backtrace;
use std::backtrace::BacktraceStatus;
use std::fmt;
use std::io;

#[derive(Clone, Copy, Debug, PartialEq, Eq)]
#[non_exhaustive]
pub enum ErrorKind {
  Unexpected,
  Unsupported,
}

impl ErrorKind {
  /// Convert self into static str.
  pub fn into_static(self) -> &'static str {
    self.into()
  }
}

impl Display for ErrorKind {
  fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
    write!(f, "{}", self.into_static())
  }
}

impl From<ErrorKind> for &'static str {
  fn from(v: ErrorKind) -> &'static str {
    match v {
      ErrorKind::Unexpected => "Unexpected",
      ErrorKind::Unsupported => "Unsupported",
    }
  }
}

pub struct InnerError {
  kind: ErrorKind,
  message: String,

  operation: &'static str,
  context: Vec<(&'static str, String)>,
  source: Option<anyhow::Error>,
  backtrace: Backtrace,
}

impl Display for InnerError {
  fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
    write!(f, "{} at {}", self.kind, self.operation)?;

    if !self.context.is_empty() {
      write!(f, ", context: {{ ")?;
      write!(
        f,
        "{}",
        self
          .context
          .iter()
          .map(|(k, v)| format!("{k}: {v}"))
          .collect::<Vec<_>>()
          .join(", ")
      )?;
      write!(f, " }}")?;
    }

    if !self.message.is_empty() {
      write!(f, " => {}", self.message)?;
    }

    if let Some(source) = &self.source {
      write!(f, ", source: {source}")?;
    }

    Ok(())
  }
}

impl Debug for InnerError {
  fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
    // If alternate has been specified, we will print like Debug.
    if f.alternate() {
      let mut de = f.debug_struct("Error");
      de.field("kind", &self.kind);
      de.field("message", &self.message);
      de.field("operation", &self.operation);
      de.field("context", &self.context);
      de.field("source", &self.source);
      return de.finish();
    }

    write!(f, "{} at {}", self.kind, self.operation)?;
    if !self.message.is_empty() {
      write!(f, " => {}", self.message)?;
    }
    writeln!(f)?;

    if !self.context.is_empty() {
      writeln!(f)?;
      writeln!(f, "Context:")?;
      for (k, v) in self.context.iter() {
        writeln!(f, "   {k}: {v}")?;
      }
    }
    if let Some(source) = &self.source {
      writeln!(f)?;
      writeln!(f, "Source:")?;
      writeln!(f, "   {source:#}")?;
    }
    if self.backtrace.status() == BacktraceStatus::Captured {
      writeln!(f)?;
      writeln!(f, "Backtrace:")?;
      writeln!(f, "{}", self.backtrace)?;
    }

    Ok(())
  }
}

impl std::error::Error for InnerError {
  fn source(&self) -> Option<&(dyn std::error::Error + 'static)> {
    self.source.as_ref().map(|v| v.as_ref())
  }
}

impl InnerError {
  /// Create a new Error with error kind and message.
  pub fn new(kind: ErrorKind, message: &str) -> Self {
    Self {
      kind,
      message: message.to_string(),

      operation: "",
      context: Vec::default(),
      source: None,
      // `Backtrace::capture()` will check if backtrace has been enabled
      // internally. It's zero cost if backtrace is disabled.
      backtrace: Backtrace::capture(),
    }
  }

  /// Set source for error.
  ///
  /// # Notes
  ///
  /// If the source has been set, we will raise a panic here.
  pub fn set_source(mut self, src: impl Into<anyhow::Error>) -> Self {
    debug_assert!(self.source.is_none(), "the source error has been set");

    self.source = Some(src.into());
    self
  }

  /// Return error's kind.
  pub fn kind(&self) -> ErrorKind {
    self.kind
  }
}

impl From<Error> for io::Error {
  fn from(err: Error) -> Self {
    io::Error::new(io::ErrorKind::Other, err)
  }
}

pub(crate) struct Error {
  inner: InnerError,
}

impl Error {
  pub(crate) fn unexpected(err: impl Into<anyhow::Error> + Display) -> Error {
    Error {
      inner: InnerError::new(ErrorKind::Unexpected, &err.to_string()).set_source(err),
    }
  }

  pub(crate) fn throw(&self, env: &mut JNIEnv) {
    if let Err(err) = self.do_throw(env) {
      match err {
        jni::errors::Error::JavaException => {
          // other calls throws exception; safely ignored
        }
        _ => env.fatal_error(err.to_string()),
      }
    }
  }

  pub(crate) fn to_exception<'local>(
    &self,
    env: &mut JNIEnv<'local>,
  ) -> jni::errors::Result<JThrowable<'local>> {
    let class = env.find_class("com/pleisto/FlappyException")?;
    let code = env.new_string(match self.inner.kind() {
      ErrorKind::Unexpected => "Unexpected",
      ErrorKind::Unsupported => "Unsupported",
    })?;
    let message = env.new_string(self.inner.to_string())?;
    let exception = env.new_object(
      class,
      "(Ljava/lang/String;Ljava/lang/String;)V",
      &[JValue::Object(&code), JValue::Object(&message)],
    )?;
    Ok(JThrowable::from(exception))
  }

  fn do_throw(&self, env: &mut JNIEnv) -> jni::errors::Result<()> {
    let exception = self.to_exception(env)?;
    env.throw(exception)
  }
}

impl From<anyhow::Error> for Error {
  fn from(error: anyhow::Error) -> Self {
    Error::unexpected(error)
  }
}

impl From<jni::errors::Error> for Error {
  fn from(error: jni::errors::Error) -> Self {
    Error::unexpected(error)
  }
}

impl From<std::str::Utf8Error> for Error {
  fn from(error: std::str::Utf8Error) -> Self {
    Error::unexpected(error)
  }
}

impl From<std::string::FromUtf8Error> for Error {
  fn from(error: std::string::FromUtf8Error) -> Self {
    Error::unexpected(error)
  }
}

impl Debug for Error {
  fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
    Debug::fmt(&self.inner, f)
  }
}

impl Display for Error {
  fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
    Display::fmt(&self.inner, f)
  }
}

impl std::error::Error for Error {
  fn source(&self) -> Option<&(dyn std::error::Error + 'static)> {
    self.inner.source()
  }
}
