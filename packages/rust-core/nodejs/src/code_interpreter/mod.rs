use flappy_common::code_interpreter::wasix::*;
use napi::bindgen_prelude::*;
use napi_derive::napi;

#[napi]
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

/// Execute a python code snippet in a wasm sandbox.
/// @param code - The python code snippet to execute.
/// @param network - Whether to allow network access.
/// @param cache_path - The path to store the wasm modules and dependencies.
/// @returns The stdout and stderr of the python code snippet.
#[napi]
pub async fn eval_python_code(
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

