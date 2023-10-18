use flappy_common::code_interpreter::wasix::*;
use std::ffi::{CStr, CString};
use std::os::raw::c_char;
use std::ptr::null;
use tokio::runtime::Builder;
use serde::{Serialize, Deserialize};
use serde_json;
use std::collections::HashMap;

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

#[derive(Debug,Serialize,Deserialize)]
struct InputStruct {
  code: String,
  network: bool,
  envs: HashMap<String,String>,
  cachePath: String
}

#[derive(Debug,Serialize,Deserialize)]
struct OutputStruct {
  stdOut: String,
  stdErr: String,
  exceptionString: String
}

/// Call python code by json in json out
#[no_mangle]
pub extern "C" fn eval_python_code_by_json(input_str: *const c_char ) -> *const c_char{
  let input_string = ptr2str(input_str);
  let input : InputStruct = serde_json::from_str(&input_string).unwrap();

  let rt = Builder::new_multi_thread().enable_all().build().unwrap();
  let output = rt.block_on(async {
    match python_sandbox(input.code, input.network, input.envs.into_iter().collect(), Some(input.cachePath)).await {
      Err(err) => OutputStruct {
        exceptionString: err.to_string(),
        stdErr: String::new(),
        stdOut: String::new(),
      },
      Ok(output) => OutputStruct {
        stdErr: output.stderr,
        stdOut: output.stdout,
        exceptionString: String::new(),
      },
    }
  });


  str2ptr(serde_json::to_string(&output).expect("Failed to serialize struct to JSON"))

}