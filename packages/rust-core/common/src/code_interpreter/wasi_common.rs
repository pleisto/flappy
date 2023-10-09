/// forked from https://github.com/wasmerio/wasmer/blob/master/lib/wasix/src/runners/wasi_common.rs
use std::{
  collections::HashMap,
  path::{Path, PathBuf},
  sync::Arc,
};

use anyhow::{Context, Error};
use futures::future::BoxFuture;
use virtual_fs::{FileSystem, FsError, OverlayFileSystem, RootFileSystemBuilder, TmpFileSystem};
use webc::metadata::annotations::Wasi as WasiAnnotation;

use wasmer_wasix::{
  bin_factory::BinaryPackage, capabilities::Capabilities, fs::default_fs_backing,
  runners::MappedDirectory, WasiEnvBuilder,
};

#[derive(Debug, Default, Clone)]
pub(crate) struct CommonWasiOptions {
  pub(crate) args: Vec<String>,
  pub(crate) env: HashMap<String, String>,
  pub(crate) forward_host_env: bool,
  pub(crate) mapped_dirs: Vec<MappedDirectory>,
  pub(crate) injected_packages: Vec<BinaryPackage>,
  pub(crate) capabilities: Capabilities,
}

impl CommonWasiOptions {
  pub(crate) fn prepare_webc_env(
    &self,
    builder: &mut WasiEnvBuilder,
    container_fs: Arc<dyn FileSystem + Send + Sync>,
    wasi: &WasiAnnotation,
    root_fs: Option<TmpFileSystem>,
  ) -> Result<(), anyhow::Error> {
    let root_fs = root_fs.unwrap_or_else(|| RootFileSystemBuilder::default().build());
    let fs = prepare_filesystem(root_fs, &self.mapped_dirs, container_fs, builder)?;

    builder.add_preopen_dir("/")?;

    if self.mapped_dirs.iter().all(|m| m.guest != ".") {
      // The user hasn't mounted "." to anything, so let's map it to "/"
      builder.add_map_dir(".", "/")?;
    }

    builder.set_fs(Box::new(fs));

    for pkg in &self.injected_packages {
      builder.add_webc(pkg.clone());
    }

    self.populate_env(wasi, builder);
    self.populate_args(wasi, builder);

    *builder.capabilities_mut() = self.capabilities.clone();

    Ok(())
  }

  fn populate_env(&self, wasi: &WasiAnnotation, builder: &mut WasiEnvBuilder) {
    for item in wasi.env.as_deref().unwrap_or_default() {
      // TODO(Michael-F-Bryan): Convert "wasi.env" in the webc crate from an
      // Option<Vec<String>> to a HashMap<String, String> so we avoid this
      // string.split() business
      match item.split_once('=') {
        Some((k, v)) => {
          builder.add_env(k, v);
        }
        None => {
          builder.add_env(item, String::new());
        }
      }
    }

    if self.forward_host_env {
      builder.add_envs(std::env::vars());
    }

    builder.add_envs(self.env.clone());
  }

  fn populate_args(&self, wasi: &WasiAnnotation, builder: &mut WasiEnvBuilder) {
    if let Some(main_args) = &wasi.main_args {
      builder.add_args(main_args);
    }

    builder.add_args(&self.args);
  }
}

type ContainerFs =
  OverlayFileSystem<TmpFileSystem, [RelativeOrAbsolutePathHack<Arc<dyn FileSystem>>; 1]>;

fn prepare_filesystem(
  root_fs: TmpFileSystem,
  mapped_dirs: &[MappedDirectory],
  container_fs: Arc<dyn FileSystem>,
  builder: &mut WasiEnvBuilder,
) -> Result<ContainerFs, Error> {
  if !mapped_dirs.is_empty() {
    let host_fs: Arc<dyn FileSystem + Send + Sync> = Arc::new(default_fs_backing());

    for dir in mapped_dirs {
      let MappedDirectory {
        host: host_path,
        guest: guest_path,
      } = dir;
      let mut guest_path = PathBuf::from(guest_path);
      tracing::debug!(
          guest=%guest_path.display(),
          host=%host_path.display(),
          "Mounting host folder",
      );

      if guest_path.is_relative() {
        guest_path = apply_relative_path_mounting_hack(&guest_path);
      }

      let host_path = std::fs::canonicalize(host_path)
        .with_context(|| format!("Unable to canonicalize host path '{}'", host_path.display()))?;

      let guest_path = root_fs
        .canonicalize_unchecked(&guest_path)
        .with_context(|| {
          format!(
            "Unable to canonicalize guest path '{}'",
            guest_path.display()
          )
        })?;

      if guest_path == Path::new("/") {
        root_fs
          .mount_directory_entries(&guest_path, &host_fs, &host_path)
          .with_context(|| format!("Unable to mount \"{}\" to root", host_path.display(),))?;
      } else {
        if let Some(parent) = guest_path.parent() {
          create_dir_all(&root_fs, parent)
            .with_context(|| format!("Unable to create the \"{}\" directory", parent.display()))?;
        }

        root_fs
          .mount(guest_path.clone(), &host_fs, host_path.clone())
          .with_context(|| {
            format!(
              "Unable to mount \"{}\" to \"{}\"",
              host_path.display(),
              guest_path.display()
            )
          })?;

        builder
          .add_preopen_dir(&guest_path)
          .with_context(|| format!("Unable to preopen \"{}\"", guest_path.display()))?;
      }
    }
  }

  // HACK(Michael-F-Bryan): The WebcVolumeFileSystem only accepts relative
  // paths, but our Python executable will try to access its standard library
  // with relative paths assuming that it is being run from the root
  // directory (i.e. it does `open("lib/python3.6/io.py")` instead of
  // `open("/lib/python3.6/io.py")`).
  // Until the FileSystem trait figures out whether relative paths should be
  // supported or not, we'll add an adapter that automatically retries
  // operations using an absolute path if it failed using a relative path.
  let container_fs = RelativeOrAbsolutePathHack(container_fs);
  let fs = OverlayFileSystem::new(root_fs, [container_fs]);

  Ok(fs)
}

/// HACK: We need this so users can mount host directories at relative paths.
/// This assumes that the current directory when a runner starts will be "/", so
/// instead of mounting to a relative path, we just mount to "/$path".
///
/// This isn't really a long-term solution because there is no guarantee what
/// the current directory will be. The WASI spec also doesn't require the
/// current directory to be part of the "main" filesystem at all, we really
/// *should* be mounting to a relative directory but that isn't supported by our
/// virtual fs layer.
///
/// See <https://github.com/wasmerio/wasmer/issues/3794> for more.
fn apply_relative_path_mounting_hack(original: &Path) -> PathBuf {
  debug_assert!(original.is_relative());

  let root = Path::new("/");
  let mapped_path = if original == Path::new(".") {
    root.to_path_buf()
  } else {
    root.join(original)
  };

  tracing::debug!(
      original_path=%original.display(),
      remapped_path=%mapped_path.display(),
      "Remapping a relative path"
  );

  mapped_path
}

fn create_dir_all(fs: &dyn FileSystem, path: &Path) -> Result<(), Error> {
  if fs.metadata(path).is_ok() {
    return Ok(());
  }

  if let Some(parent) = path.parent() {
    create_dir_all(fs, parent)?;
  }

  fs.create_dir(path)?;

  Ok(())
}

#[derive(Debug)]
struct RelativeOrAbsolutePathHack<F>(F);

impl<F: FileSystem> RelativeOrAbsolutePathHack<F> {
  fn execute<Func, Ret>(&self, path: &Path, operation: Func) -> Result<Ret, FsError>
  where
    Func: Fn(&F, &Path) -> Result<Ret, FsError>,
  {
    // First, try it with the path we were given
    let result = operation(&self.0, path);

    if result.is_err() && !path.is_absolute() {
      // we were given a relative path, but maybe the operation will work
      // using absolute paths instead.
      let path = Path::new("/").join(path);
      operation(&self.0, &path)
    } else {
      result
    }
  }
}

impl<F: FileSystem> virtual_fs::FileSystem for RelativeOrAbsolutePathHack<F> {
  fn read_dir(&self, path: &Path) -> virtual_fs::Result<virtual_fs::ReadDir> {
    self.execute(path, |fs, p| fs.read_dir(p))
  }

  fn create_dir(&self, path: &Path) -> virtual_fs::Result<()> {
    self.execute(path, |fs, p| fs.create_dir(p))
  }

  fn remove_dir(&self, path: &Path) -> virtual_fs::Result<()> {
    self.execute(path, |fs, p| fs.remove_dir(p))
  }

  fn rename<'a>(&'a self, from: &Path, to: &Path) -> BoxFuture<'a, virtual_fs::Result<()>> {
    let from = from.to_owned();
    let to = to.to_owned();
    Box::pin(async move { self.0.rename(&from, &to).await })
  }

  fn metadata(&self, path: &Path) -> virtual_fs::Result<virtual_fs::Metadata> {
    self.execute(path, |fs, p| fs.metadata(p))
  }

  fn remove_file(&self, path: &Path) -> virtual_fs::Result<()> {
    self.execute(path, |fs, p| fs.remove_file(p))
  }

  fn new_open_options(&self) -> virtual_fs::OpenOptions {
    virtual_fs::OpenOptions::new(self)
  }
}

impl<F: FileSystem> virtual_fs::FileOpener for RelativeOrAbsolutePathHack<F> {
  fn open(
    &self,
    path: &Path,
    conf: &virtual_fs::OpenOptionsConfig,
  ) -> virtual_fs::Result<Box<dyn virtual_fs::VirtualFile + Send + Sync + 'static>> {
    self.execute(path, |fs, p| {
      fs.new_open_options().options(conf.clone()).open(p)
    })
  }
}
