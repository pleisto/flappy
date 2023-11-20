#![forbid(unsafe_code)]

pub mod client;
pub mod error;
pub mod model;
pub mod options;

#[cfg(feature = "openai")]
pub mod openai;

#[cfg(feature = "local")]
pub mod local;
