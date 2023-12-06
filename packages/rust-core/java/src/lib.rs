use std::cell::RefCell;
use std::ffi::c_void;

use jni::sys::jint;
use jni::sys::JNI_VERSION_1_8;
use jni::JNIEnv;
use jni::JavaVM;
use once_cell::sync::OnceCell;
use tokio::runtime::Builder;
use tokio::runtime::Runtime;

mod convert;
mod error;
mod future;
mod sandbox;

use mimalloc::MiMalloc;

/// mimalloc is a compact general purpose allocator with excellent performance.
/// https://github.com/microsoft/mimalloc
#[global_allocator]
static GLOBAL: MiMalloc = MiMalloc;

pub(crate) type Result<T> = std::result::Result<T, error::Error>;

static mut RUNTIME: OnceCell<Runtime> = OnceCell::new();
thread_local! {
    static ENV: RefCell<Option<*mut jni::sys::JNIEnv>> = RefCell::new(None);
}

/// # Safety
///
/// This function could be only called by java vm when loading this lib.
#[no_mangle]
pub unsafe extern "system" fn JNI_OnLoad(vm: JavaVM, _: *mut c_void) -> jint {
  RUNTIME
    .set(
      Builder::new_multi_thread()
        .worker_threads(num_cpus::get())
        .on_thread_start(move || {
          ENV.with(|cell| {
            let env = vm.attach_current_thread_as_daemon().unwrap();
            *cell.borrow_mut() = Some(env.get_raw());
          })
        })
        .enable_all()
        .build()
        .unwrap(),
    )
    .unwrap();

  JNI_VERSION_1_8
}

/// # Safety
///
/// This function could be only called by java vm when unloading this lib.
#[no_mangle]
pub unsafe extern "system" fn JNI_OnUnload(_: JavaVM, _: *mut c_void) {
  if let Some(r) = RUNTIME.take() {
    r.shutdown_background()
  }
}

/// # Safety
///
/// This function could be only called when the lib is loaded and within a RUNTIME-spawned thread.
unsafe fn get_current_env<'local>() -> JNIEnv<'local> {
  let env = ENV.with(|cell| *cell.borrow_mut()).unwrap();
  JNIEnv::from_raw(env).unwrap()
}

/// # Safety
///
/// This function could be only called when the lib is loaded.
unsafe fn get_global_runtime<'local>() -> &'local Runtime {
  RUNTIME.get_unchecked()
}
