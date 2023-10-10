use std::{sync::mpsc, thread, time::Duration};

use flappy_common::code_interpreter::wasix::*;

// This is the interface to the JVM that we'll call the majority of our
// methods on.
use jni::JNIEnv;

// These objects are what you should use as arguments to your native
// function. They carry extra lifetime information to prevent them escaping
// this context and getting used after being GC'd.
use jni::objects::{JClass, JObject, JString};

// This is just a pointer. We'll be returning it from our function. We
// can't return one of the objects with lifetime information because the
// lifetime checker won't let us.
use jni::sys::jint;

// This keeps Rust from "mangling" the name and making it unique for this
// crate.
// #[no_mangle]
// pub extern "system" fn Java_flappy_CodeInterpreter_evalPythonCode<'local>(
//   mut env: JNIEnv<'local>,
//   // This is the class that owns our static method. It's not going to be used,
//   // but still must be present to match the expected signature of a static
//   // native method.
//   _class: JClass<'local>,
//   callback: JObject,
//   // code: JString<'local>,
// ) {
//   let jvm = env.get_java_vm().unwrap();

//   // First, we have to get the string out of Java. Check out the `strings`
//   // module for more info on how this works.
//   // let code: String = env
//   //   .get_string(&code)
//   //   .expect("Couldn't get java string!")
//   //   .into();

//   // let network: bool = env.ge

//   // Then we have to create a new Java string to return. Again, more info
//   // in the `strings` module.
//   // let output = env
//   //   .new_string(format!("Hello, {}", code))
//   //   .expect("Couldn't create java string!");
//   // // output.into_raw()

//   let network: bool = true;
//   let envs: Vec<(String, String)> = vec![];
//   let cache_path: Option<String> = Default::default();

//   let callback = env.new_global_ref(callback).unwrap();

//   let (tx, rx) = mpsc::channel();

//   let _ = thread::spawn(move || {
//     // Signal that the thread has started.
//     tx.send(()).unwrap();

//     // Use the `JavaVM` interface to attach a `JNIEnv` to the current thread.
//     let mut env = jvm.attach_current_thread().unwrap();

//     // for i in 0..11 {
//     //   let progress = (i * 10) as jint;
//     //   // Now we can use all available `JNIEnv` functionality normally.
//     //   env
//     //     .call_method(&callback, "asyncCallback", "(I)V", &[progress.into()])
//     //     .unwrap();
//     let progress = (0 * 10) as jint;

//     env
//       .call_method(&callback, "asyncCallback", "(I)V", &[progress.into()])
//       .unwrap();
//     thread::sleep(Duration::from_millis(100));

//     // The current thread is detached automatically when `env` goes out of scope.
//   });

//   // Wait until the thread has started.
//   rx.recv().unwrap();

//   // match python_sandbox(code, network, envs, cache_path).await {
//   //   Ok(sandbox_output) => env.new_string(sandbox_output.stdout).expect("").into_raw(),
//   //   Err(err) => env.new_string(err.to_string()).expect("").into_raw(),
//   // }

//   // python_sandbox(code, network, envs, cache_path)
//   // output.into_raw()
//   // Finally, extract the raw pointer to return.
// }

#[no_mangle]
pub extern "system" fn Java_flappy_CodeInterpreter_evalPythonCode(
  mut env: JNIEnv,
  _class: JClass,
  callback: JObject,
  code: JString,
) {
  // `JNIEnv` cannot be sent across thread boundaries. To be able to use JNI
  // functions in other threads, we must first obtain the `JavaVM` interface
  // which, unlike `JNIEnv` is `Send`.
  let jvm = env.get_java_vm().unwrap();

  // We need to obtain global reference to the `callback` object before sending
  // it to the thread, to prevent it from being collected by the GC.
  let callback = env.new_global_ref(callback).unwrap();

  // Use channel to prevent the Java program to finish before the thread
  // has chance to start.
  let (tx, rx) = mpsc::channel();

  let code: String = env
    .get_string(&code)
    .expect("Couldn't get java string!")
    .into();

  let network: bool = true;
  let envs: Vec<(String, String)> = vec![];
  let cache_path: Option<String> = Default::default();

  let _ = thread::spawn(move || {
    // Signal that the thread has started.
    tx.send(()).unwrap();

    // Use the `JavaVM` interface to attach a `JNIEnv` to the current thread.
    let mut env = jvm.attach_current_thread().unwrap();

    // for i in 0..11 {
    let progress = (3 * 10) as jint;
    // Now we can use all available `JNIEnv` functionality normally.
    env
      .call_method(&callback, "asyncCallback", "(I)V", &[progress.into()])
      .unwrap();
    // thread::sleep(Duration::from_millis(100));
    // }

    let result = python_sandbox(code, network, envs, cache_path);

    // match python_sandbox(code, network, envs, cache_path).await {
    //   Ok(sandbox_output) => env.new_string(sandbox_output.stdout).expect("").into_raw(),
    //   Err(err) => env.new_string(err.to_string()).expect("").into_raw(),
    // }

    // The current thread is detached automatically when `env` goes out of scope.
  });

  // Wait until the thread has started.
  rx.recv().unwrap();
}
