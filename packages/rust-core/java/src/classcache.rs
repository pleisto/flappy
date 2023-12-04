use ::jni::errors::Result;
use dashmap::DashMap;
use jni::{
  objects::{GlobalRef, JClass},
  JNIEnv,
};
use once_cell::sync::OnceCell;

#[allow(dead_code)]
static CLASSCACHE: OnceCell<DashMap<String, GlobalRef>> = OnceCell::new();

pub(crate) fn find_add_class<'local>(env: &mut JNIEnv<'local>, classname: &str) -> Result<()> {
  let cache = CLASSCACHE.get_or_init(DashMap::new);
  let obj: JClass<'local> = env.find_class(classname).unwrap();
  cache.insert(classname.to_owned(), env.new_global_ref(obj).unwrap());
  Ok(())
}

pub(crate) fn get_class(classname: &str) -> Option<GlobalRef> {
  let cache = CLASSCACHE.get_or_init(DashMap::new);
  cache.get(classname).map(|pair| pair.value().clone())
}
