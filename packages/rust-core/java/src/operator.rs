use std::str::FromStr;
use std::time::Duration;

use flappy_common::code_interpreter::wasix::python_sandbox;
use flappy_common::code_interpreter::wasix::SandboxOutput;
use jni::objects::JByteArray;
use jni::objects::JClass;
use jni::objects::JObject;
use jni::objects::JString;
use jni::objects::JValue;
use jni::objects::JValueOwned;
use jni::sys::{jlong, jobject};
use jni::JNIEnv;
use opendal::layers::BlockingLayer;
use opendal::raw::PresignedRequest;
use opendal::Operator;
use opendal::Scheme;

use crate::get_current_env;
use crate::get_global_runtime;
use crate::jmap_to_hashmap;
use crate::jstring_to_string;
use crate::make_operator_info;
use crate::make_presigned_request;
use crate::Result;

#[no_mangle]
pub extern "system" fn Java_com_pleisto_Operator_constructor(
  mut env: JNIEnv,
  _: JClass,
  scheme: JString,
  map: JObject,
) -> jlong {
  intern_constructor(&mut env, scheme, map).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_constructor(env: &mut JNIEnv, scheme: JString, map: JObject) -> Result<jlong> {
  let scheme = Scheme::from_str(jstring_to_string(env, &scheme)?.as_str())?;
  let map = jmap_to_hashmap(env, &map)?;
  let mut op = Operator::via_map(scheme, map)?;
  if !op.info().full_capability().blocking {
    let _guard = unsafe { get_global_runtime() }.enter();
    op = op.layer(BlockingLayer::create()?);
  }
  Ok(Box::into_raw(Box::new(op)) as jlong)
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_disposeInternal(
  _: JNIEnv,
  _: JObject,
  op: *mut Operator,
) {
  drop(Box::from_raw(op));
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_write(
  mut env: JNIEnv,
  _: JClass,
  op: *mut Operator,
  path: JString,
  content: JByteArray,
) -> jlong {
  intern_write(&mut env, op, path, content).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_write(
  env: &mut JNIEnv,
  op: *mut Operator,
  path: JString,
  content: JByteArray,
) -> Result<jlong> {
  let op = unsafe { &mut *op };
  let id = request_id(env)?;

  let path = jstring_to_string(env, &path)?;
  let content = env.convert_byte_array(content)?;

  unsafe { get_global_runtime() }.spawn(async move {
    let result = do_write(op, path, content).await;
    complete_future(id, result.map(|_| JValueOwned::Void))
  });

  Ok(id)
}

async fn do_write(op: &mut Operator, path: String, content: Vec<u8>) -> Result<()> {
  Ok(op.write(&path, content).await?)
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_append(
  mut env: JNIEnv,
  _: JClass,
  op: *mut Operator,
  path: JString,
  content: JByteArray,
) -> jlong {
  intern_append(&mut env, op, path, content).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_append(
  env: &mut JNIEnv,
  op: *mut Operator,
  path: JString,
  content: JByteArray,
) -> Result<jlong> {
  let op = unsafe { &mut *op };
  let id = request_id(env)?;

  let path = jstring_to_string(env, &path)?;
  let content = env.convert_byte_array(content)?;

  unsafe { get_global_runtime() }.spawn(async move {
    let result = do_append(op, path, content).await;
    complete_future(id, result.map(|_| JValueOwned::Void))
  });

  Ok(id)
}

async fn do_append(op: &mut Operator, path: String, content: Vec<u8>) -> Result<()> {
  Ok(op.write_with(&path, content).append(true).await?)
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_stat(
  mut env: JNIEnv,
  _: JClass,
  op: *mut Operator,
  path: JString,
) -> jlong {
  intern_stat(&mut env, op, path).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_stat(env: &mut JNIEnv, op: *mut Operator, path: JString) -> Result<jlong> {
  let op = unsafe { &mut *op };
  let id = request_id(env)?;

  let path = jstring_to_string(env, &path)?;

  unsafe { get_global_runtime() }.spawn(async move {
    let result = do_stat(op, path).await;
    complete_future(id, result.map(JValueOwned::Long))
  });

  Ok(id)
}

async fn do_stat(op: &mut Operator, path: String) -> Result<jlong> {
  let metadata = op.stat(&path).await?;
  Ok(Box::into_raw(Box::new(metadata)) as jlong)
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_read(
  mut env: JNIEnv,
  _: JClass,
  op: *mut Operator,
  path: JString,
) -> jlong {
  intern_read(&mut env, op, path).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_read(env: &mut JNIEnv, op: *mut Operator, path: JString) -> Result<jlong> {
  let op = unsafe { &mut *op };
  let id = request_id(env)?;

  let path = jstring_to_string(env, &path)?;

  unsafe { get_global_runtime() }.spawn(async move {
    let result = do_read(op, path).await;
    complete_future(id, result.map(JValueOwned::Object))
  });

  Ok(id)
}

async fn do_read<'local>(op: &mut Operator, path: String) -> Result<JObject<'local>> {
  let content = op.read(&path).await?;

  let env = unsafe { get_current_env() };
  let result = env.byte_array_from_slice(content.as_slice())?;
  Ok(result.into())
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_delete(
  mut env: JNIEnv,
  _: JClass,
  op: *mut Operator,
  path: JString,
) -> jlong {
  intern_delete(&mut env, op, path).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_delete(env: &mut JNIEnv, op: *mut Operator, path: JString) -> Result<jlong> {
  let op = unsafe { &mut *op };
  let id = request_id(env)?;

  let path = jstring_to_string(env, &path)?;

  unsafe { get_global_runtime() }.spawn(async move {
    let result = do_delete(op, path).await;
    complete_future(id, result.map(|_| JValueOwned::Void))
  });

  Ok(id)
}

async fn do_delete(op: &mut Operator, path: String) -> Result<()> {
  Ok(op.delete(&path).await?)
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_makeBlockingOp(
  _: JNIEnv,
  _: JClass,
  op: *mut Operator,
) -> jlong {
  let op = unsafe { &mut *op };
  Box::into_raw(Box::new(op.blocking())) as jlong
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_makeOperatorInfo(
  mut env: JNIEnv,
  _: JClass,
  op: *mut Operator,
) -> jobject {
  intern_make_operator_info(&mut env, op).unwrap_or_else(|e| {
    e.throw(&mut env);
    JObject::default().into_raw()
  })
}

fn intern_make_operator_info(env: &mut JNIEnv, op: *mut Operator) -> Result<jobject> {
  let op = unsafe { &mut *op };
  Ok(make_operator_info(env, op.info())?.into_raw())
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_createDir(
  mut env: JNIEnv,
  _: JClass,
  op: *mut Operator,
  path: JString,
) -> jlong {
  intern_create_dir(&mut env, op, path).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_create_dir(env: &mut JNIEnv, op: *mut Operator, path: JString) -> Result<jlong> {
  let op = unsafe { &mut *op };
  let id = request_id(env)?;

  let path = jstring_to_string(env, &path)?;

  unsafe { get_global_runtime() }.spawn(async move {
    let result = do_create_dir(op, path).await;
    complete_future(id, result.map(|_| JValueOwned::Void))
  });

  Ok(id)
}

async fn do_create_dir(op: &mut Operator, path: String) -> Result<()> {
  Ok(op.create_dir(&path).await?)
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_copy(
  mut env: JNIEnv,
  _: JClass,
  op: *mut Operator,
  source_path: JString,
  target_path: JString,
) -> jlong {
  intern_copy(&mut env, op, source_path, target_path).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_copy(
  env: &mut JNIEnv,
  op: *mut Operator,
  source_path: JString,
  target_path: JString,
) -> Result<jlong> {
  let op = unsafe { &mut *op };
  let id = request_id(env)?;

  let source_path = jstring_to_string(env, &source_path)?;
  let target_path = jstring_to_string(env, &target_path)?;

  unsafe { get_global_runtime() }.spawn(async move {
    let result = do_copy(op, source_path, target_path).await;
    complete_future(id, result.map(|_| JValueOwned::Void))
  });

  Ok(id)
}

async fn do_copy(op: &mut Operator, source_path: String, target_path: String) -> Result<()> {
  Ok(op.copy(&source_path, &target_path).await?)
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_rename(
  mut env: JNIEnv,
  _: JClass,
  op: *mut Operator,
  source_path: JString,
  target_path: JString,
) -> jlong {
  intern_rename(&mut env, op, source_path, target_path).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_rename(
  env: &mut JNIEnv,
  op: *mut Operator,
  source_path: JString,
  target_path: JString,
) -> Result<jlong> {
  let op = unsafe { &mut *op };
  let id = request_id(env)?;

  let source_path = jstring_to_string(env, &source_path)?;
  let target_path = jstring_to_string(env, &target_path)?;

  unsafe { get_global_runtime() }.spawn(async move {
    let result = do_rename(op, source_path, target_path).await;
    complete_future(id, result.map(|_| JValueOwned::Void))
  });

  Ok(id)
}

async fn do_rename(op: &mut Operator, source_path: String, target_path: String) -> Result<()> {
  Ok(op.rename(&source_path, &target_path).await?)
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_presignRead(
  mut env: JNIEnv,
  _: JClass,
  op: *mut Operator,
  path: JString,
  expire: jlong,
) -> jlong {
  intern_presign_read(&mut env, op, path, expire).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_presign_read(
  env: &mut JNIEnv,
  op: *mut Operator,
  path: JString,
  expire: jlong,
) -> Result<jlong> {
  let op = unsafe { &mut *op };
  let id = request_id(env)?;

  let path = jstring_to_string(env, &path)?;
  let expire = Duration::from_nanos(expire as u64);

  unsafe { get_global_runtime() }.spawn(async move {
    let result = do_presign_read(op, path, expire).await;
    let mut env = unsafe { get_current_env() };
    let result = result.and_then(|req| make_presigned_request(&mut env, req));
    complete_future(id, result.map(JValueOwned::Object))
  });

  Ok(id)
}

async fn do_presign_read(
  op: &mut Operator,
  path: String,
  expire: Duration,
) -> Result<PresignedRequest> {
  Ok(op.presign_read(&path, expire).await?)
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_presignWrite(
  mut env: JNIEnv,
  _: JClass,
  op: *mut Operator,
  path: JString,
  expire: jlong,
) -> jlong {
  intern_presign_write(&mut env, op, path, expire).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_presign_write(
  env: &mut JNIEnv,
  op: *mut Operator,
  path: JString,
  expire: jlong,
) -> Result<jlong> {
  let op = unsafe { &mut *op };
  let id = request_id(env)?;

  let path = jstring_to_string(env, &path)?;
  let expire = Duration::from_nanos(expire as u64);

  unsafe { get_global_runtime() }.spawn(async move {
    let result = do_presign_write(op, path, expire).await;
    let mut env = unsafe { get_current_env() };
    let result = result.and_then(|req| make_presigned_request(&mut env, req));
    complete_future(id, result.map(JValueOwned::Object))
  });

  Ok(id)
}

async fn do_presign_write(
  op: &mut Operator,
  path: String,
  expire: Duration,
) -> Result<PresignedRequest> {
  Ok(op.presign_write(&path, expire).await?)
}

/// # Safety
///
/// This function should not be called before the Operator are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_presignStat(
  mut env: JNIEnv,
  _: JClass,
  op: *mut Operator,
  path: JString,
  expire: jlong,
) -> jlong {
  intern_presign_stat(&mut env, op, path, expire).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

fn intern_presign_stat(
  env: &mut JNIEnv,
  op: *mut Operator,
  path: JString,
  expire: jlong,
) -> Result<jlong> {
  let op = unsafe { &mut *op };
  let id = request_id(env)?;

  let path = jstring_to_string(env, &path)?;
  let expire = Duration::from_nanos(expire as u64);

  unsafe { get_global_runtime() }.spawn(async move {
    let result = do_presign_stat(op, path, expire).await;
    let mut env = unsafe { get_current_env() };
    let result = result.and_then(|req| make_presigned_request(&mut env, req));
    complete_future(id, result.map(JValueOwned::Object))
  });

  Ok(id)
}

async fn do_presign_stat(
  op: &mut Operator,
  path: String,
  expire: Duration,
) -> Result<PresignedRequest> {
  Ok(op.presign_stat(&path, expire).await?)
}

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
        "com/pleisto/Operator$AsyncRegistry",
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
        "com/pleisto/Operator$AsyncRegistry",
        "get",
        "(J)Ljava/util/concurrent/CompletableFuture;",
        &[JValue::Long(id)],
      )?
      .l()?,
  )
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Operator_evalPythonCode(
  mut env: JNIEnv,
  _: JClass,
  op: *mut Operator,
  code: JString,
) -> jlong {
  intern_eval_python_code(&mut env, op, code).unwrap_or_else(|e| {
    e.throw(&mut env);
    0
  })
}

#[derive(Clone, Debug, Default)]
pub struct SandboxStdResult {
  stdout: String,
  stderr: String,
}

impl SandboxStdResult {
  pub fn stdout(&self) -> &str {
    &self.stdout
  }

  pub fn set_stdout(&mut self, stdout: &str) -> &mut Self {
    self.stdout = stdout.to_string();
    self
  }

  pub fn stderr(&self) -> &str {
    &self.stderr
  }

  pub fn set_stderr(&mut self, stderr: &str) -> &mut Self {
    self.stderr = stderr.to_string();
    self
  }
}

#[derive(Clone, Debug, Default)]
pub struct SandboxResult(SandboxStdResult);

impl SandboxResult {
  pub fn new(acc: SandboxStdResult) -> Self {
    SandboxResult(acc)
  }

  pub fn stdout(&self) -> &str {
    self.0.stdout()
  }

  pub fn stderr(&self) -> &str {
    self.0.stderr()
  }
}

fn make_sandbox_output<'a>(env: &mut JNIEnv<'a>, info: SandboxOutput) -> Result<JObject<'a>> {
  let stdout = env.new_string(info.stdout)?;
  let stderr = env.new_string(info.stderr)?;

  let result = env.new_object(
    "com/pleisto/SandboxResult",
    "(Ljava/lang/String;Ljava/lang/String;)V",
    &[JValue::Object(&stdout), JValue::Object(&stderr)],
  )?;
  Ok(result)
}

fn intern_eval_python_code(env: &mut JNIEnv, op: *mut Operator, code: JString) -> Result<jlong> {
  let op = unsafe { &mut *op };
  let id = request_id(env)?;

  let code = jstring_to_string(env, &code)?;

  let network: bool = false;
  let envs: Vec<(String, String)> = vec![];
  let cache_path: Option<String> = Default::default();

  unsafe { get_global_runtime() }.spawn(async move {
    let sandbox_result: Result<SandboxOutput> = python_sandbox(code, network, envs, cache_path)
      .await
      .map_err(|err| err.into());
    let mut env = unsafe { get_current_env() };
    let result = sandbox_result.and_then(|req| make_sandbox_output(&mut env, req));
    complete_future(id, result.map(JValueOwned::Object))
  });

  Ok(id)
}
