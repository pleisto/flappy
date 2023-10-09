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
*/

use anyhow::{Context, Error};
use reqwest::Client;
use std::{
  path::{Path, PathBuf},
  time::{Duration, SystemTime},
};
use wasmer_cache::Hash;
use wasmer_registry::package::Package;

// Default registry is the WAPM registry.
const DEFAULT_REGISTRY: &str = "https://wapm.io/";

// Cache invalidation threshold is 5 minutes.
const CACHE_INVALIDATION_THRESHOLD: Duration = Duration::from_secs(5 * 60);

#[derive(Debug, Clone, PartialEq)]
enum CacheInfo {
  /// Caching has been disabled.
  Disabled,
  /// An item isn't in the cache, but could be cached later on.
  Miss,
  /// An item in the cache.
  Hit {
    path: PathBuf,
    etag: Option<String>,
    last_modified: Option<SystemTime>,
  },
}

impl CacheInfo {
  fn for_url(key: &Hash, checkout_dir: &Path, disabled: bool) -> CacheInfo {
    if disabled {
      return CacheInfo::Disabled;
    }

    let path = checkout_dir.join(key.to_string());

    if !path.exists() {
      return CacheInfo::Miss;
    }

    let etag = std::fs::read_to_string(path.with_extension("etag")).ok();
    let last_modified = path.metadata().and_then(|m| m.modified()).ok();

    CacheInfo::Hit {
      etag,
      last_modified,
      path,
    }
  }
}

fn classify_cache_using_mtime(info: CacheInfo) -> Result<PathBuf, CacheState> {
  let (path, last_modified, etag) = match info {
    CacheInfo::Hit {
      path,
      last_modified: Some(last_modified),
      etag,
      ..
    } => (path, last_modified, etag),
    CacheInfo::Hit {
      path,
      last_modified: None,
      etag: Some(etag),
      ..
    } => return Err(CacheState::PossiblyDirty { etag, path }),
    CacheInfo::Hit {
      etag: None,
      last_modified: None,
      path,
      ..
    } => {
      return Err(CacheState::UnableToVerify { path });
    }
    CacheInfo::Disabled | CacheInfo::Miss { .. } => return Err(CacheState::Miss),
  };

  if let Ok(time_since_last_modified) = last_modified.elapsed() {
    if time_since_last_modified <= CACHE_INVALIDATION_THRESHOLD {
      return Ok(path);
    }
  }

  match etag {
    Some(etag) => Err(CacheState::PossiblyDirty { etag, path }),
    None => Err(CacheState::UnableToVerify { path }),
  }
}

/// Classification of how valid an item is based on filesystem metadata.
#[derive(Debug)]
enum CacheState {
  /// The item isn't in the cache.
  Miss,
  /// The cached item might be invalid, but it has an ETag we can use for
  /// further validation.
  PossiblyDirty { etag: String, path: PathBuf },
  /// The cached item exists on disk, but we weren't able to tell whether it is still
  /// valid, and there aren't any other ways to validate it further. You can
  /// probably reuse this if you are having internet issues.
  UnableToVerify { path: PathBuf },
}

impl CacheState {
  fn take_path(self) -> Option<PathBuf> {
    match self {
      CacheState::PossiblyDirty { path, .. } | CacheState::UnableToVerify { path } => Some(path),
      _ => None,
    }
  }

  fn use_etag_to_resolve_cached_file(self, new_etag: Option<&str>) -> Option<PathBuf> {
    match (new_etag, self) {
      (
        Some(new_etag),
        CacheState::PossiblyDirty {
          etag: cached_etag,
          path,
        },
      ) if cached_etag == new_etag => Some(path),
      _ => None,
    }
  }
}

/**
 * Get the package from the registry and cache it locally.
 */
pub async fn get_package(
  name: String,
  cache_dir: PathBuf,
  registry: Option<String>,
) -> Result<PathBuf, Error> {
  let package: Package = name.parse().unwrap();
  let registry = registry.unwrap_or_else(|| DEFAULT_REGISTRY.to_string());
  let url = package.url(registry.as_str()).unwrap();
  let pkg_hash = Hash::generate(url.to_string().as_bytes());
  let cache_info = CacheInfo::for_url(&pkg_hash, &cache_dir, false);
  // If the cache is hit, directly return the path.
  let state = match classify_cache_using_mtime(cache_info) {
    Ok(path) => {
      return Ok(path);
    }
    Err(s) => s,
  };

  // Download the package when the cache is miss.
  let client = Client::new();
  let request = client.get(url.clone()).header("Accept", "application/webc");
  let response = match request.send().await {
    Ok(r) => r
      .error_for_status()
      .with_context(|| format!("Failed to download package from {}", url))?,
    Err(e) => {
      // Something went wrong. If it was a connection issue and we've got a cached file, let's use that and emit a warning.
      if e.is_connect() {
        if let Some(path) = state.take_path() {
          return Ok(path);
        }
      }

      return Err(Error::from(e).context(format!("Failed to download package from {}", url)));
    }
  };

  // If it has an ETag, we can use that to validate the cache.
  let etag = response
    .headers()
    .get("ETag")
    .and_then(|h| h.to_str().ok())
    .map(|etag| etag.trim().to_string());
  if let Some(cached) = state.use_etag_to_resolve_cached_file(etag.as_deref()) {
    return Ok(cached);
  }

  let file_path = cache_dir.join(pkg_hash.to_string());
  let mut f = tokio::fs::File::create(file_path.clone())
    .await
    .with_context(|| {
      format!(
        "Failed to create cache file {}",
        cache_dir.join(pkg_hash.to_string()).display()
      )
    })?;

  tokio::io::copy(&mut &*response.bytes().await?, &mut f).await?;
  f.sync_all().await?;
  if let Some(etag) = etag {
    let etag_path = file_path.as_path().with_extension("etag");
    tokio::fs::write(etag_path, etag).await?;
  }

  Ok(file_path)
}
