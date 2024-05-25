use jni::objects::JObject;
use jni::objects::JValue;
use jni::objects::JValueOwned;
use jni::sys::jlong;
use jni::JNIEnv;

use crate::get_current_env;
use crate::Result;

fn make_object<'local>(
  env: &mut JNIEnv<'local>,
  value: JValueOwned<'local>,
) -> Result<JObject<'local>> {
  let o = match value {
    JValueOwned::Object(o) => o,
    JValueOwned::Byte(_) => env.new_object("java/lang/Long", "(B)V", &[value.borrow()])?,
    JValueOwned::Char(_) => env.new_object("java/lang/Char", "(C)V", &[value.borrow()])?,
    JValueOwned::Short(_) => env.new_object("java/lang/Short", "(S)V", &[value.borrow()])?,
    JValueOwned::Int(_) => env.new_object("java/lang/Integer", "(I)V", &[value.borrow()])?,
    JValueOwned::Long(_) => env.new_object("java/lang/Long", "(J)V", &[value.borrow()])?,
    JValueOwned::Bool(_) => env.new_object("java/lang/Boolean", "(Z)V", &[value.borrow()])?,
    JValueOwned::Float(_) => env.new_object("java/lang/Float", "(F)V", &[value.borrow()])?,
    JValueOwned::Double(_) => env.new_object("java/lang/Double", "(D)V", &[value.borrow()])?,
    JValueOwned::Void => JObject::null(),
  };
  Ok(o)
}

pub(crate) fn complete_future(id: jlong, result: Result<JValueOwned>) {
  let mut env = unsafe { get_current_env() };
  let future = get_future(&mut env, id).unwrap();
  match result {
    Ok(result) => {
      let result = make_object(&mut env, result).unwrap();
      env
        .call_method(
          future,
          "complete",
          "(Ljava/lang/Object;)Z",
          &[JValue::Object(&result)],
        )
        .unwrap()
    }
    Err(err) => {
      let exception = err.to_exception(&mut env).unwrap();
      env
        .call_method(
          future,
          "completeExceptionally",
          "(Ljava/lang/Throwable;)Z",
          &[JValue::Object(&exception)],
        )
        .unwrap()
    }
  };
}

pub(crate) fn request_id(env: &mut JNIEnv) -> Result<jlong> {
  Ok(
    env
      .call_static_method("com/pleisto/FlappyAsyncRegistry", "requestId", "()J", &[])?
      .j()?,
  )
}

fn get_future<'local>(env: &mut JNIEnv<'local>, id: jlong) -> Result<JObject<'local>> {
  Ok(
    env
      .call_static_method(
        "com/pleisto/FlappyAsyncRegistry",
        "get",
        "(J)Ljava/util/concurrent/CompletableFuture;",
        &[JValue::Long(id)],
      )?
      .l()?,
  )
}

#[cfg(test)]
mod test {
  use super::*;
  use crate::test_utils;

  #[test]
  fn test_request_id() {
    test_utils::JVM_ENV.with(|env| {
      let result = request_id(unsafe { &mut env.unsafe_clone() });
      assert!(result.is_ok())
    });
  }
}
