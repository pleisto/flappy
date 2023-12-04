use jni::{
  descriptors::Desc,
  errors::Error,
  objects::{JClass, JObject, JThrowable, JValue},
  JNIEnv,
};
use std::{
  any::Any,
  convert::TryFrom,
  panic::{catch_unwind, resume_unwind, UnwindSafe},
  sync::MutexGuard,
};

/// Result from [`try_block`]. This object can be chained into
/// [`catch`](TryCatchResult::catch) calls to catch exceptions. When finished
/// with the try/catch sequence, the result can be obtained from
/// [`result`](TryCatchResult::result).
pub struct TryCatchResult<'a: 'b, 'b, T> {
  env: &'b JNIEnv<'a>,
  try_result: Result<Result<T, Error>, Error>,
  catch_result: Option<Result<T, Error>>,
}

/// Attempt to execute a block of JNI code. If the code causes an exception
/// to be thrown, it will be stored in the resulting [`TryCatchResult`] for
/// matching with [`catch`](TryCatchResult::catch). If an exception was already
/// being thrown before [`try_block`] is called, the given block will not be
/// executed, nor will any of the [`catch`](TryCatchResult::catch) blocks.
///
/// # Arguments
///
/// * `env` - Java environment to use.
/// * `block` - Block of JNI code to run.
pub fn try_block<'a: 'b, 'b, T>(
  env: &'b JNIEnv<'a>,
  block: impl FnOnce() -> Result<T, Error>,
) -> TryCatchResult<'a, 'b, T> {
  TryCatchResult {
    env,
    try_result: (|| {
      if env.exception_check()? {
        Err(Error::JavaException)
      } else {
        Ok(block())
      }
    })(),
    catch_result: None,
  }
}

impl<'a: 'b, 'b, T> TryCatchResult<'a, 'b, T> {
  /// Attempt to catch an exception thrown by [`try_block`]. If the thrown
  /// exception matches the given class, the block is executed. If no
  /// exception was thrown by [`try_block`], or if the exception does not
  /// match the given class, the block is not executed.
  ///
  /// # Arguments
  ///
  /// * `class` - Exception class to match.
  /// * `block` - Block of JNI code to run.
  pub fn catch(
    self,
    class: impl Desc<'a, JClass<'a>>,
    block: impl FnOnce(JThrowable<'a>) -> Result<T, Error>,
  ) -> Self {
    match (self.try_result, self.catch_result) {
      (Err(e), _) => Self {
        env: self.env,
        try_result: Err(e),
        catch_result: None,
      },
      (Ok(Ok(r)), _) => Self {
        env: self.env,
        try_result: Ok(Ok(r)),
        catch_result: None,
      },
      (Ok(Err(e)), Some(r)) => Self {
        env: self.env,
        try_result: Ok(Err(e)),
        catch_result: Some(r),
      },
      (Ok(Err(Error::JavaException)), None) => {
        let env = self.env;
        let catch_result = (|| {
          if env.exception_check()? {
            let ex = env.exception_occurred()?;
            let _auto_local = env.auto_local(ex);
            env.exception_clear()?;
            if env.is_instance_of(ex, class)? {
              return block(ex).map(|o| Some(o));
            }
            env.throw(ex)?;
          }
          Ok(None)
        })()
        .transpose();
        Self {
          env,
          try_result: Ok(Err(Error::JavaException)),
          catch_result,
        }
      }
      (Ok(Err(e)), None) => Self {
        env: self.env,
        try_result: Ok(Err(e)),
        catch_result: None,
      },
    }
  }

  /// Get the result of the try/catch sequence. If [`try_block`] succeeded,
  /// or if one of the [`catch`](TryCatchResult::catch) calls succeeded, its
  /// result is returned.
  pub fn result(self) -> Result<T, Error> {
    match (self.try_result, self.catch_result) {
      (Err(e), _) => Err(e),
      (Ok(Ok(r)), _) => Ok(r),
      (Ok(Err(_)), Some(r)) => r,
      (Ok(Err(e)), None) => Err(e),
    }
  }
}

/// Wrapper for [`JObject`]s that implement
/// `io.github.gedgygedgy.rust.panic.PanicException`. Provides methods to get
/// and take the associated [`Any`].
///
/// Looks up the class and method IDs on creation rather than for every method
/// call.
pub struct JPanicException<'a: 'b, 'b> {
  internal: JThrowable<'a>,
  env: &'b JNIEnv<'a>,
}

impl<'a: 'b, 'b> JPanicException<'a, 'b> {
  /// Create a [`JPanicException`] from the environment and an object. This
  /// looks up the necessary class and method IDs to call all of the methods
  /// on it so that extra work doesn't need to be done on every method call.
  ///
  /// # Arguments
  ///
  /// * `env` - Java environment to use.
  /// * `obj` - Object to wrap.
  pub fn from_env(env: &'b JNIEnv<'a>, obj: JThrowable<'a>) -> Result<Self, Error> {
    Ok(Self { internal: obj, env })
  }

  /// Create a new `PanicException` from the given [`Any`].
  ///
  /// # Arguments
  ///
  /// * `env` - Java environment to use.
  /// * `any` - [`Any`] to put in the `PanicException`.
  pub fn new(env: &'b JNIEnv<'a>, any: Box<dyn Any + Send + 'static>) -> Result<Self, Error> {
    let msg = if let Some(s) = any.downcast_ref::<&str>() {
      env.new_string(s)?
    } else if let Some(s) = any.downcast_ref::<String>() {
      env.new_string(s)?
    } else {
      JObject::null().into()
    };

    let obj = env.new_object(
      "io/github/gedgygedgy/rust/panic/PanicException",
      "(Ljava/lang/String;)V",
      &[JValue::Object(&msg)],
    )?;
    unsafe { env.set_rust_field(obj, "any", any) }?;
    Self::from_env(env, obj.into())
  }

  /// Borrows the [`Any`] associated with the exception.
  pub fn get(&self) -> Result<MutexGuard<Box<dyn Any + Send + 'static>>, Error> {
    unsafe { self.env.get_rust_field(self.internal, "any") }
  }

  /// Takes the [`Any`] associated with the exception.
  pub fn take(&self) -> Result<Box<dyn Any + Send + 'static>, Error> {
    unsafe { self.env.take_rust_field(self.internal, "any") }
  }

  /// Resumes unwinding using the [`Any`] associated with the exception.
  pub fn resume_unwind(&self) -> Result<(), Error> {
    resume_unwind(self.take()?);
  }
}

impl<'a: 'b, 'b> TryFrom<JPanicException<'a, 'b>> for Box<dyn Any + Send + 'static> {
  type Error = Error;

  fn try_from(ex: JPanicException<'a, 'b>) -> Result<Self, Error> {
    ex.take()
  }
}

impl<'a: 'b, 'b> From<JPanicException<'a, 'b>> for JThrowable<'a> {
  fn from(ex: JPanicException<'a, 'b>) -> Self {
    ex.internal
  }
}

impl<'a: 'b, 'b> ::std::ops::Deref for JPanicException<'a, 'b> {
  type Target = JThrowable<'a>;

  fn deref(&self) -> &Self::Target {
    &self.internal
  }
}

/// Calls the given closure. If it panics, catch the unwind, wrap it in a
/// `io.github.gedgygedgy.rust.panic.PanicException`, and throw it.
///
/// # Arguments
///
/// * `env` - Java environment to use.
/// * `f` - Closure to call.
pub fn throw_unwind<R>(
  env: &JNIEnv<'_>,
  f: impl FnOnce() -> R + UnwindSafe,
) -> Result<R, Result<(), Error>> {
  catch_unwind(f).map_err(|e| {
    let old_ex = if env.exception_check()? {
      let ex = env.exception_occurred()?;
      env.exception_clear()?;
      Some(ex)
    } else {
      None
    };
    let ex = JPanicException::new(env, e)?;

    if let Some(old_ex) = old_ex {
      env.call_method(
        *ex,
        "addSuppressed",
        "(Ljava/lang/Throwable;)V",
        &[JValue::Object(&ex)],
      )?;
    }
    let ex: JThrowable = ex.into();
    env.throw(ex)?;
    Ok(())
  })
}
