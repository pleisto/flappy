use std::collections::HashMap;

use crate::Result;
use jni::objects::JMap;
use jni::objects::JObject;
use jni::objects::JString;
use jni::sys::jboolean;
use jni::sys::JNI_TRUE;
use jni::JNIEnv;

pub(crate) fn jstring_to_string(env: &mut JNIEnv, s: &JString) -> Result<String> {
  let res = unsafe { env.get_string_unchecked(s)? };
  Ok(res.into())
}

pub(crate) fn jstring_to_option_string(env: &mut JNIEnv, s: &JString) -> Result<Option<String>> {
  let s: String = jstring_to_string(env, s)?;

  if s.is_empty() {
    Ok(Default::default())
  } else {
    Ok(Some(s))
  }
}

pub(crate) fn jboolean_to_bool(_env: &mut JNIEnv, bol: jboolean) -> Result<bool> {
  Ok(bol == JNI_TRUE)
}

pub(crate) fn jmap_to_vec_string_string(
  env: &mut JNIEnv,
  params: &JObject,
) -> Result<Vec<(String, String)>> {
  let envs: Vec<(String, String)> = jmap_to_hashmap(env, params)?.into_iter().collect();
  Ok(envs)
}

pub(crate) fn jmap_to_hashmap(
  env: &mut JNIEnv,
  params: &JObject,
) -> Result<HashMap<String, String>> {
  let map = JMap::from_env(env, params)?;
  let mut iter = map.iter(env)?;

  let mut result: HashMap<String, String> = HashMap::new();
  while let Some(e) = iter.next(env)? {
    let k = JString::from(e.0);
    let v = JString::from(e.1);
    result.insert(env.get_string(&k)?.into(), env.get_string(&v)?.into());
  }

  Ok(result)
}
