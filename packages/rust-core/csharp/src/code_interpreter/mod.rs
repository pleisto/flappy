use flappy_common::code_interpreter::wasix::*;
use std::ffi::{CStr, CString};
use std::os::raw::c_char;
use std::ptr::null;
use tokio::runtime::Builder;
#[repr(C)]
pub struct StdOutput {
  pub stdout: *const c_char,
  pub stderr: *const c_char,
  pub exception_message: *const c_char,
}

#[repr(C)]
pub struct DictonaryType {
  pub key: *const c_char,
  pub value: *const c_char,
}

fn str2ptr(str: String) -> *const c_char {
  CString::new(str)
    .expect("Failed to convert String to CString")
    .into_raw()
}

fn ptr2str(c_char_ptr: *const std::os::raw::c_char) -> String {
  unsafe {
    if c_char_ptr.is_null() {
      // 处理空指针情况
      return String::new();
    }

    let c_str = CStr::from_ptr(c_char_ptr);
    let string_result = c_str.to_str();

    match string_result {
      Ok(string) => string.to_owned(),
      Err(_) => String::new(),
    }
  }
}



/// Just of Native Call Test
#[no_mangle]
pub extern "C" fn eval_native_call() -> bool {
  true
}

/// Execute a python code snippet in a wasm sandbox.
/// @param code - The python code snippet to execute.
/// @param network - Whether to allow network access.
/// @param cache_path - The path to store the wasm modules and dependencies.
/// @param envs - The environment of code execute
/// @param envs_count - The count of environment array size
/// @returns The stdout and stderr of the python code snippet.
#[no_mangle]
pub extern "C" fn eval_python_code(
  code: *const c_char,
  network: bool,
  envs: *const DictonaryType,
  envs_count: usize,
  cache_path: *const c_char,
) -> StdOutput {
  let rt = Builder::new_multi_thread().enable_all().build().unwrap();
  rt.block_on(async {
    let mut vec_type: Vec<(String, String)> = Vec::new();

    unsafe {
      for obj in std::slice::from_raw_parts(envs, envs_count) {
        vec_type.push((ptr2str(obj.key), ptr2str(obj.value)));
      }
    }

    let code_str = ptr2str(code);
    let cache_path_str = ptr2str(cache_path);

    match python_sandbox(code_str, network, vec_type, Some(cache_path_str)).await {
      Err(err) => StdOutput {
        stderr: null(),
        stdout: null(),
        exception_message: str2ptr(err.to_string()),
      },
      Ok(output) => StdOutput {
        stderr: str2ptr(output.stderr),
        stdout: str2ptr(output.stdout),
        exception_message: null(),
      },
    }
  })
}
