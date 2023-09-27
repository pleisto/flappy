/*
Copyright 2023 Pleisto Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This is WASIX-compatible wasm runtime as a Code Interpreter sandbox.
See https://wasix.org/ for more information.
*/
use super::{
    std_io::{BufferedVirtualFile, Stderr, Stdout},
    wasix_runner::WasixRunner,
    wasmer_pkg::get_package,
};
use anyhow::Context;
use std::{
    path::PathBuf,
    str,
    sync::{Arc, Mutex},
};
use wasmer::Store;
use wasmer_wasix::{
    bin_factory::BinaryPackage,
    capabilities::Capabilities,
    runners::Runner,
    runtime::{
        module_cache::{FileSystemCache, ModuleCache, SharedCache},
        package_loader::BuiltinPackageLoader,
        task_manager::tokio::TokioTaskManager,
    },
    PluggableRuntime, Runtime,
};
use webc::Container;

const PYTHON_INTERPRETER_WASM_PKG: &str = "wasmer/python";

/**
 * Execute a python code snippet in a wasm sandbox
 */
pub async fn python_sandbox(
    code: String,
    cache_path: Option<String>,
    network: bool,
) -> anyhow::Result<()> {
    // Default to no network access on the sandbox
    //let network = network.unwrap_or(false);

    // Set the cache path to store the wasm modules and dependencies
    let cache_path = cache_path.map(PathBuf::from).unwrap_or_else(|| {
        dirs::cache_dir()
            .unwrap_or_else(|| std::env::temp_dir())
            .join("flappy/code-interpreter")
    });
    std::fs::create_dir_all(&cache_path).context("Failed to create cache directory")?;

    // Get python interpreter web container from the wasmer registry
    let file_path = get_package(
        PYTHON_INTERPRETER_WASM_PKG.to_string(),
        cache_path.clone(),
        None,
    )
    .await?;
    let container = Container::from_disk(file_path)?;

    // Create a capability set
    let mut caps = Capabilities::default();
    if network {
        // allow http requests
        caps.http_client = wasmer_wasix::http::HttpClientCapabilityV1::new_allow_all();
    }
    caps.threading.enable_asynchronous_threading = true;

    // Create a runtime
    let runtime = wasix_runtime(cache_path, network);
    let bin_pkg = BinaryPackage::from_webc(&container, &runtime)
        .await
        .unwrap();
    let stderr = Arc::new(Mutex::new(
        Box::new(Stderr::default()) as Box<dyn BufferedVirtualFile + Send + Sync>
    ));
    let stdout = Arc::new(Mutex::new(
        Box::new(Stdout::default()) as Box<dyn BufferedVirtualFile + Send + Sync>
    ));
    let runner = WasixRunner::new()
        .with_stdout(Arc::clone(&stdout))
        .with_stderr(Arc::clone(&stderr))
        .with_capabilities(caps);
    let result = runner
        .with_args(["-c", &code])
        .run_command("python", &bin_pkg, Arc::new(runtime));
    println!(
        "stdout: {:?}",
        String::from_utf8_lossy(stdout.lock().unwrap().get_buf())
    );
    println!(
        "stderr: {:?}",
        String::from_utf8_lossy(stderr.lock().unwrap().get_buf())
    );
    result
}

/**
 * Create a WASIX-compatible runtime
 */
fn wasix_runtime(cache_path: PathBuf, network: bool) -> impl Runtime + Send + Sync {
    wasmer_vm::set_stack_size(1048576);
    let store = Store::default();
    let tokio_rt = tokio::runtime::Builder::new_multi_thread()
        .enable_all()
        .build()
        .unwrap();
    let mut rt = PluggableRuntime::new(Arc::new(TokioTaskManager::new(tokio_rt)));
    let cache = SharedCache::default().with_fallback(FileSystemCache::new(cache_path.clone()));

    let client = Arc::new(wasmer_wasix::http::default_http_client().unwrap());

    rt.set_engine(Some(store.engine().clone()))
        .set_module_cache(cache)
        .set_package_loader(BuiltinPackageLoader::new_with_client(cache_path, client));

    if network {
        // TODO: fix me.
        // Forwards socket, tcp, and udp calls to the host
        rt.set_networking_implementation(virtual_net::host::LocalNetworking::default());
    }

    rt
}

#[cfg(test)]
mod tests {
    use super::*;

    #[tokio::test]
    async fn test_python_sandbox() {
        let code = "print('hello world')";
        //let std_out = Box::new(Stdout::default());
        let result = python_sandbox(code.to_string(), None, true);
        println!("result: {:?}", result.await);
        //assert!(result.await.is_ok());
    }
}
