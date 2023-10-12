use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;

#[no_mangle]
pub extern "system" fn Java_HelloWorld_hello<'local>(
  mut env: JNIEnv<'local>,
  class: JClass<'local>,
  input: JString<'local>,
) -> jstring {
  let input: String = env
    .get_string(&input)
    .expect("Couldn't get java string!")
    .into();

  let output = env
    .new_string(format!("Hello, {}!", input))
    .expect("Couldn't create java string!");

  output.into_raw()
}

pub struct StdOutput {
  pub stdout: String,
  pub stderr: String,
}

impl From<SandboxOutput> for StdOutput {
  fn from(output: SandboxOutput) -> Self {
    Self {
      stdout: output.stdout,
      stderr: output.stderr,
    }
  }
}

async fn eval_python_code(
  code: String,
  network: bool,
  envs: Vec<(String, String)>,
  cache_path: Option<String>,
) -> Result<StdOutput> {
  match python_sandbox(code, network, envs, cache_path).await {
    Ok(output) => Ok(output.into()),
    Err(err) => Err(Error::from_reason(err.to_string())),
  }
}
