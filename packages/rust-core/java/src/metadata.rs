use jni::objects::JClass;
use jni::objects::JObject;
use jni::sys::jboolean;
use jni::sys::jlong;
use jni::JNIEnv;
use opendal::Metadata;

/// # Safety
///
/// This function should not be called before the Stat are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Metadata_isFile(
  _: JNIEnv,
  _: JClass,
  ptr: *mut Metadata,
) -> jboolean {
  let metadata = &mut *ptr;
  metadata.is_file() as jboolean
}

/// # Safety
///
/// This function should not be called before the Stat are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Metadata_getContentLength(
  _: JNIEnv,
  _: JClass,
  ptr: *mut Metadata,
) -> jlong {
  let metadata = &mut *ptr;
  metadata.content_length() as jlong
}

/// # Safety
///
/// This function should not be called before the Stat are ready.
#[no_mangle]
pub unsafe extern "system" fn Java_com_pleisto_Metadata_disposeInternal(
  _: JNIEnv,
  _: JObject,
  ptr: *mut Metadata,
) {
  drop(Box::from_raw(ptr));
}
