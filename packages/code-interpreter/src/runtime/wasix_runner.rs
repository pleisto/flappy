/// Forked from https://github.com/wasmerio/wasmer/blob/master/lib/wasix/src/runners/wasi.rs
/// change ArcFile type defined.
use std::sync::{Arc, Mutex};

use super::wasi_common::CommonWasiOptions;
use super::{arc_box_file::ArcBoxFile, std_io::BufferedVirtualFile};
use anyhow::{Context, Error};
use virtual_fs::TmpFileSystem;
use wasmer_wasix::{
    bin_factory::BinaryPackage, capabilities::Capabilities, runners::MappedDirectory, Runtime,
    WasiEnvBuilder,
};
use webc::metadata::{annotations::Wasi, Command};

#[derive(Debug, Default, Clone)]
pub struct WasixRunner {
    wasi: CommonWasiOptions,
    stdin: Option<ArcBoxFile>,
    stdout: Option<ArcBoxFile>,
    stderr: Option<ArcBoxFile>,
}

impl WasixRunner {
    /// Constructs a new `WasiRunner`.
    pub fn new() -> Self {
        WasixRunner::default()
    }

    #[allow(dead_code)]
    /// Returns the current arguments for this `WasiRunner`
    pub fn get_args(&self) -> Vec<String> {
        self.wasi.args.clone()
    }

    /// Builder method to provide CLI args to the runner
    pub fn with_args<A, S>(mut self, args: A) -> Self
    where
        A: IntoIterator<Item = S>,
        S: Into<String>,
    {
        self.set_args(args);
        self
    }

    /// Set the CLI args
    pub fn set_args<A, S>(&mut self, args: A)
    where
        A: IntoIterator<Item = S>,
        S: Into<String>,
    {
        self.wasi.args = args.into_iter().map(|s| s.into()).collect();
    }

    #[allow(dead_code)]
    /// Builder method to provide environment variables to the runner.
    pub fn with_env(mut self, key: impl Into<String>, value: impl Into<String>) -> Self {
        self.set_env(key, value);
        self
    }

    #[allow(dead_code)]
    /// Provide environment variables to the runner.
    pub fn set_env(&mut self, key: impl Into<String>, value: impl Into<String>) {
        self.wasi.env.insert(key.into(), value.into());
    }

    #[allow(dead_code)]
    pub fn with_envs<I, K, V>(mut self, envs: I) -> Self
    where
        I: IntoIterator<Item = (K, V)>,
        K: Into<String>,
        V: Into<String>,
    {
        self.set_envs(envs);
        self
    }

    #[allow(dead_code)]
    pub fn set_envs<I, K, V>(&mut self, envs: I)
    where
        I: IntoIterator<Item = (K, V)>,
        K: Into<String>,
        V: Into<String>,
    {
        for (key, value) in envs {
            self.wasi.env.insert(key.into(), value.into());
        }
    }

    #[allow(dead_code)]
    pub fn with_forward_host_env(mut self) -> Self {
        self.set_forward_host_env();
        self
    }

    #[allow(dead_code)]
    pub fn set_forward_host_env(&mut self) {
        self.wasi.forward_host_env = true;
    }

    #[allow(dead_code)]
    pub fn with_mapped_directories<I, D>(mut self, dirs: I) -> Self
    where
        I: IntoIterator<Item = D>,
        D: Into<MappedDirectory>,
    {
        self.wasi
            .mapped_dirs
            .extend(dirs.into_iter().map(|d| d.into()));
        self
    }

    /// Add a package that should be available to the instance at runtime.
    pub fn add_injected_package(&mut self, pkg: BinaryPackage) -> &mut Self {
        self.wasi.injected_packages.push(pkg);
        self
    }

    /// Add a package that should be available to the instance at runtime.
    #[allow(dead_code)]
    pub fn with_injected_package(mut self, pkg: BinaryPackage) -> Self {
        self.add_injected_package(pkg);
        self
    }

    /// Add packages that should be available to the instance at runtime.
    pub fn add_injected_packages(
        &mut self,
        packages: impl IntoIterator<Item = BinaryPackage>,
    ) -> &mut Self {
        self.wasi.injected_packages.extend(packages);
        self
    }

    /// Add packages that should be available to the instance at runtime.
    #[allow(dead_code)]
    pub fn with_injected_packages(
        mut self,
        packages: impl IntoIterator<Item = BinaryPackage>,
    ) -> Self {
        self.add_injected_packages(packages);
        self
    }

    #[allow(dead_code)]
    pub fn capabilities(&mut self) -> &mut Capabilities {
        &mut self.wasi.capabilities
    }

    pub fn with_capabilities(mut self, capabilities: Capabilities) -> Self {
        self.set_capabilities(capabilities);
        self
    }

    pub fn set_capabilities(&mut self, capabilities: Capabilities) {
        self.wasi.capabilities = capabilities;
    }

    #[allow(dead_code)]
    pub fn with_stdin(
        mut self,
        stdin: Arc<Mutex<Box<dyn BufferedVirtualFile + Send + Sync>>>,
    ) -> Self {
        self.set_stdin(stdin);
        self
    }

    #[allow(dead_code)]
    pub fn set_stdin(
        &mut self,
        stdin: Arc<Mutex<Box<dyn BufferedVirtualFile + Send + Sync>>>,
    ) -> &mut Self {
        self.stdin = Some(ArcBoxFile::new(stdin));
        self
    }

    pub fn with_stdout(
        mut self,
        stdout: Arc<Mutex<Box<dyn BufferedVirtualFile + Send + Sync>>>,
    ) -> Self {
        self.set_stdout(stdout);
        self
    }

    pub fn set_stdout(
        &mut self,
        stdout: Arc<Mutex<Box<dyn BufferedVirtualFile + Send + Sync>>>,
    ) -> &mut Self {
        self.stdout = Some(ArcBoxFile::new(stdout));
        self
    }

    pub fn with_stderr(
        mut self,
        stderr: Arc<Mutex<Box<dyn BufferedVirtualFile + Send + Sync>>>,
    ) -> Self {
        self.set_stderr(stderr);
        self
    }

    pub fn set_stderr(
        &mut self,
        stderr: Arc<Mutex<Box<dyn BufferedVirtualFile + Send + Sync>>>,
    ) -> &mut Self {
        self.stderr = Some(ArcBoxFile::new(stderr));
        self
    }

    pub(crate) fn prepare_webc_env(
        &self,
        program_name: &str,
        wasi: &Wasi,
        pkg: &BinaryPackage,
        runtime: Arc<dyn Runtime + Send + Sync>,
        root_fs: Option<TmpFileSystem>,
    ) -> Result<WasiEnvBuilder, anyhow::Error> {
        let mut builder = WasiEnvBuilder::new(program_name);
        let container_fs = Arc::clone(&pkg.webc_fs);
        self.wasi
            .prepare_webc_env(&mut builder, container_fs, wasi, root_fs)?;

        if let Some(stdin) = &self.stdin {
            builder.set_stdin(Box::new(stdin.clone()));
        }
        if let Some(stdout) = &self.stdout {
            builder.set_stdout(Box::new(stdout.clone()));
        }
        if let Some(stderr) = &self.stderr {
            builder.set_stderr(Box::new(stderr.clone()));
        }

        builder.add_webc(pkg.clone());
        builder.set_runtime(runtime);

        Ok(builder)
    }
}

impl wasmer_wasix::runners::Runner for WasixRunner {
    fn can_run_command(command: &Command) -> Result<bool, Error> {
        Ok(command
            .runner
            .starts_with(webc::metadata::annotations::WASI_RUNNER_URI))
    }

    #[tracing::instrument(skip_all)]
    fn run_command(
        &mut self,
        command_name: &str,
        pkg: &BinaryPackage,
        runtime: Arc<dyn Runtime + Send + Sync>,
    ) -> Result<(), Error> {
        let cmd = pkg
            .get_command(command_name)
            .with_context(|| format!("The package doesn't contain a \"{command_name}\" command"))?;
        let wasi = cmd
            .metadata()
            .annotation("wasi")?
            .unwrap_or_else(|| Wasi::new(command_name));

        let module = runtime.load_module_sync(cmd.atom())?;
        let mut store = runtime.new_store();

        let env = self
            .prepare_webc_env(command_name, &wasi, pkg, runtime, None)
            .context("Unable to prepare the WASI environment")?;

        if self
            .wasi
            .capabilities
            .threading
            .enable_asynchronous_threading
        {
            env.run_with_store_async(module, store)?;
        } else {
            env.run_with_store(module, &mut store)?;
        }

        Ok(())
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn send_and_sync() {
        fn assert_send<T: Send>() {}
        fn assert_sync<T: Sync>() {}

        assert_send::<WasixRunner>();
        assert_sync::<WasixRunner>();
    }
}
