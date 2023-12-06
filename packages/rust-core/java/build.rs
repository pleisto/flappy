#[cfg(feature = "build-java-support")]
fn build_java() {
  use std::{env, path::PathBuf, process::Command};

  let java_src_dir = PathBuf::from(env::var("CARGO_MANIFEST_DIR").unwrap());
  println!("src dir {:?}", java_src_dir);

  let mut java_src_mvnw = java_src_dir.clone();
  java_src_mvnw.push(
    #[cfg(target_os = "windows")]
    "mvnw.bat",
    #[cfg(not(target_os = "windows"))]
    "mvnw",
  );

  let java_build_dir = PathBuf::from(env::var("OUT_DIR").unwrap());

  println!("build dir {:?}", java_build_dir);

  let mut binding = Command::new(java_src_mvnw.clone());
  let command = binding
    .args(["package", "-DskipTests", "-Dgpg.skip"])
    .current_dir(java_src_dir.clone());

  println!("command {:?}", command);

  let result = command.spawn().unwrap().wait().unwrap();

  if result.success() {
    let mut source_dir = java_src_dir;
    let version_u8 = Command::new(java_src_mvnw)
      .args([
        "help:evaluate",
        "-Dexpression=project.version",
        "-q",
        "-DforceStdout",
      ])
      .output()
      .unwrap()
      .stdout;
    let version = String::from_utf8_lossy(&version_u8);
    let trimed_version = version.trim();

    source_dir.push("target");
    source_dir.push(format!("flappy-java-bindings-{}.jar", trimed_version));

    let mut target_dir = java_build_dir;
    target_dir.push("flappy-java-bindings-latest.jar");

    println!("cargo:warning=build ok {:?}", target_dir);
    std::fs::copy(source_dir, target_dir).unwrap();
  } else {
    panic!("build error");
  }
}

fn main() {
  println!("cargo:warning=build maven");
  println!("cargo:rerun-if-changed=src/main");

  #[cfg(feature = "build-java-support")]
  build_java();
}
