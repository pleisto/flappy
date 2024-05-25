use jni::{objects::GlobalRef, JNIEnv, JavaVM};
use lazy_static::lazy_static;

pub(crate) struct GlobalJVM {
  pub(crate) jvm: JavaVM,
  pub(crate) class_loader: GlobalRef,
}

thread_local! {
    pub static JVM_ENV: JNIEnv<'static> = {
        let mut env = JVM.jvm.attach_current_thread_permanently().unwrap();

        let thread = env
            .call_static_method(
                "java/lang/Thread",
                "currentThread",
                "()Ljava/lang/Thread;",
                &[],
            )
            .unwrap()
            .l()
            .unwrap();
        env.call_method(
            thread,
            "setContextClassLoader",
            "(Ljava/lang/ClassLoader;)V",
            &[JVM.class_loader.as_obj().into()]
        ).unwrap();

        env
    }
}

lazy_static! {
  static ref JVM: GlobalJVM = {
    use jni::InitArgsBuilder;
    use std::env;

    let mut jni_utils_jar = std::path::PathBuf::from(env::var("OUT_DIR").unwrap());
    jni_utils_jar.push("flappy-java-bindings-latest.jar");
    let option = format!("-Djava.class.path={}", jni_utils_jar.to_str().unwrap());

    let jvm_args = InitArgsBuilder::new().option(option).build().unwrap();
    let jvm = JavaVM::new(jvm_args).unwrap();

    let mut env = jvm.attach_current_thread_permanently().unwrap();

    let thread = env
      .call_static_method(
        "java/lang/Thread",
        "currentThread",
        "()Ljava/lang/Thread;",
        &[],
      )
      .unwrap()
      .l()
      .unwrap();
    let class_loader = env
      .call_method(
        thread,
        "getContextClassLoader",
        "()Ljava/lang/ClassLoader;",
        &[],
      )
      .unwrap()
      .l()
      .unwrap();
    let class_loader = env.new_global_ref(class_loader).unwrap();

    GlobalJVM { jvm, class_loader }
  };
}
