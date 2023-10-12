use std::fmt::Debug;
use std::fmt::Display;
use std::fmt::Formatter;

use jni::objects::JThrowable;
use jni::objects::JValue;
use jni::JNIEnv;
use opendal::ErrorKind;

pub(crate) struct Error {
  inner: opendal::Error,
}

impl Error {
  pub(crate) fn unexpected(err: impl Into<anyhow::Error> + Display) -> Error {
    Error {
      inner: opendal::Error::new(ErrorKind::Unexpected, &err.to_string()).set_source(err),
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
      ErrorKind::ConfigInvalid => "ConfigInvalid",
      ErrorKind::NotFound => "NotFound",
      ErrorKind::PermissionDenied => "PermissionDenied",
      ErrorKind::IsADirectory => "IsADirectory",
      ErrorKind::NotADirectory => "NotADirectory",
      ErrorKind::AlreadyExists => "AlreadyExists",
      ErrorKind::RateLimited => "RateLimited",
      ErrorKind::IsSameFile => "IsSameFile",
      ErrorKind::ConditionNotMatch => "ConditionNotMatch",
      ErrorKind::ContentTruncated => "ContentTruncated",
      ErrorKind::ContentIncomplete => "ContentIncomplete",
      ErrorKind::InvalidInput => "InvalidInput",
      _ => "Unexpected",
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

impl From<opendal::Error> for Error {
  fn from(error: opendal::Error) -> Self {
    Self { inner: error }
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
