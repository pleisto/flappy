use flappy_common::code_interpreter::wasix::prepare_sandbox;
use flappy_common::code_interpreter::wasix::python_sandbox;
use flappy_common::code_interpreter::wasix::SandboxOutput;
use jni::objects::JClass;
use jni::objects::JObject;
use jni::objects::JString;
use jni::objects::JValue;
use jni::objects::JValueOwned;
use jni::sys::jboolean;
use jni::sys::jlong;
use jni::sys::jstring;
use jni::JNIEnv;

use crate::get_current_env;
use crate::get_global_runtime;
use crate::jboolean_to_bool;
use crate::jmap_to_vec_string_string;
use crate::jstring_to_option_string;
use crate::jstring_to_string;
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

fn complete_future(id: jlong, result: Result<JValueOwned>) {
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

fn request_id(env: &mut JNIEnv) -> Result<jlong> {
  Ok(
    env
      .call_static_method(
        "com/pleisto/FlappyJniSandbox$AsyncRegistry",
        "requestId",
        "()J",
        &[],
      )?
      .j()?,
  )
}

fn get_future<'local>(env: &mut JNIEnv<'local>, id: jlong) -> Result<JObject<'local>> {
  Ok(
    env
      .call_static_method(
        "com/pleisto/FlappyJniSandbox$AsyncRegistry",
        "get",
        "(J)Ljava/util/concurrent/CompletableFuture;",
        &[JValue::Long(id)],
      )?
      .l()?,
  )
}

#[no_mangle]
pub extern "system" fn Java_com_pleisto_FlappyJniSandbox_ping<'local>(
  env: JNIEnv<'local>,
  _: JClass<'local>,
) -> jstring {
  let output = env
    .new_string("pong".to_string())
    .expect("Couldn't create java string!");
  output.into_raw()
}

#[no_mangle]
pub extern "system" fn Java_com_pleisto_FlappyJniDummy_echo(
  mut env: JNIEnv,
  _: JClass,
  code: JString,
  network: jboolean,
  envs: JObject,
  cache_path: JString,
) -> jstring {
  intern_echo(&mut env, code, network, envs, cache_path).unwrap()
}

fn intern_echo(
  env: &mut JNIEnv,
  code: JString,
  network: jboolean,
  envs: JObject,
  cache_path: JString,
) -> Result<jstring> {
  let code = jstring_to_string(env, &code)?;
  let option_cache_path = jstring_to_option_string(env, &cache_path)?;
  let envs: Vec<(String, String)> = jmap_to_vec_string_string(env, &envs)?;
  let network = jboolean_to_bool(env, network)?;

  let output = env.new_string(format!(
    "code: {}, network: {}, envs: {:?}, cache_path: {:?}",
    code, network, envs, option_cache_path
  ))?;
  Ok(output.into_raw())
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_FlappyJniSandbox_nativePrepareSandbox(
  mut env: JNIEnv,
  _: JClass,
  cache_path: JString,
) -> jlong {
  intern_prepare_sandbox(&mut env, cache_path).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_prepare_sandbox(env: &mut JNIEnv, cache_path: JString) -> Result<jlong> {
  let id = request_id(env)?;
  let option_cache_path = jstring_to_option_string(env, &cache_path)?;

  unsafe { get_global_runtime() }.spawn(async move {
    let result = prepare_sandbox(option_cache_path)
      .await
      .map_err(|err| err.into())
      .map(|_| JValueOwned::Void);
    complete_future(id, result)
  });

  Ok(id)
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_FlappyJniSandbox_nativeEvalPythonCode(
  mut env: JNIEnv,
  _: JClass,
  code: JString,
  network: jboolean,
  envs: JObject,
  cache_path: JString,
) -> jlong {
  intern_eval_python_code(&mut env, code, network, envs, cache_path).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_eval_python_code(
  env: &mut JNIEnv,
  code: JString,
  network: jboolean,
  envs: JObject,
  cache_path: JString,
) -> Result<jlong> {
  let id = request_id(env)?;

  let code = jstring_to_string(env, &code)?;
  let option_cache_path = jstring_to_option_string(env, &cache_path)?;
  let envs = jmap_to_vec_string_string(env, &envs)?;
  let network = jboolean_to_bool(env, network)?;

  unsafe { get_global_runtime() }.spawn(async move {
    let sandbox_result: Result<SandboxOutput> =
      python_sandbox(code, network, envs, option_cache_path)
        .await
        .map_err(|err| err.into());
    let mut env = unsafe { get_current_env() };
    let result = sandbox_result.and_then(|req| make_sandbox_output(&mut env, req));
    complete_future(id, result.map(JValueOwned::Object))
  });

  Ok(id)
}

fn make_sandbox_output<'a>(env: &mut JNIEnv<'a>, info: SandboxOutput) -> Result<JObject<'a>> {
  let stdout = env.new_string(info.stdout)?;
  let stderr = env.new_string(info.stderr)?;

  let result = env.new_object(
    "com/pleisto/FlappyJniSandboxResult",
    "(Ljava/lang/String;Ljava/lang/String;)V",
    &[JValue::Object(&stdout), JValue::Object(&stderr)],
  )?;
  Ok(result)
}
