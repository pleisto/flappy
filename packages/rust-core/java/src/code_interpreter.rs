use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;

#[derive(Clone, Debug, Default)]
pub struct SandboxStdOutput {
  stdout: String,
  stderr: String,
}

impl SandboxStdOutput {
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
pub struct SandboxOutput(SandboxStdOutput);

impl SandboxOutput {
  pub fn new(acc: SandboxStdOutput) -> Self {
    SandboxOutput(acc)
  }

  pub fn stdout(&self) -> &str {
    self.0.stdout()
  }

  pub fn stderr(&self) -> &str {
    self.0.stderr()
  }
}

fn make_sandbox_output<'a>(env: &mut JNIEnv<'a>, info: SandboxOutput) -> Result<JObject<'a>> {
  let stdout = env.new_string(info.stdout().to_string())?;
  let stderr = env.new_string(info.stderr().to_string())?;

  let result = env.new_object(
    "com/pleisto/SandboxOutput",
    "(Ljava/lang/String;Ljava/lang/String;)V",
    &[JValue::Object(&stdout), JValue::Object(&stderr)],
  )?;
  Ok(result)
}

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
