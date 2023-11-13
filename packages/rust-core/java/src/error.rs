use std::fmt::Debug;

use jni::{
  objects::{JThrowable, JValue},
  JNIEnv,
};

#[derive(Clone, Copy, Debug, PartialEq, Eq)]
#[non_exhaustive]
pub(crate) enum ErrorKind {
  Unexpected,
  #[allow(dead_code)]
  Unsupported,
}

impl ToString for ErrorKind {
  fn to_string(&self) -> String {
    match self {
      ErrorKind::Unexpected => "Unexpected".to_owned(),
      ErrorKind::Unsupported => "Unsupported".to_owned(),
    }
  }
}

#[derive(thiserror::Error, Debug)]
pub enum Error {
  #[error("IO error: {0}")]
  IO(#[from] std::io::Error),

  #[error("JNI error: {0}")]
  Jni(#[from] jni::errors::Error),

  #[error("Unexpected error: {0}")]
  Anyhow(#[from] anyhow::Error),
}

impl Error {
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
    let class = env.find_class("com/pleisto/FlappyJniException")?;

    let kind = match self {
      Error::IO(_) => ErrorKind::Unexpected,
      Error::Jni(_) => ErrorKind::Unexpected,
      Error::Anyhow(_) => ErrorKind::Unexpected,
    };

    let code = env.new_string(kind.to_string())?;
    let message = env.new_string(self.to_string())?;
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
