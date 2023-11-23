use std::{convert::Infallible, io::Write, path::PathBuf};

use anyhow::anyhow;
use async_trait::async_trait;
use derive_builder::Builder;
use llm::{
  InferenceParameters, InferenceRequest, LoadProgress, Model, ModelArchitecture, ModelParameters,
  TokenizerSource,
};
use tracing::{debug, info};

use crate::{
  client::Client,
  error::{ClientCreationError, ExecutorError},
  model::Output,
  options::{BuiltinOptions, Options},
};

#[derive(Builder)]
#[builder(default)]
pub struct LocalOptions {
  model_arch: Option<String>,
  model_path: Option<PathBuf>,
  /// The context size ("memory") the model should use when evaluating a prompt. A larger context
  /// consumes more resources, but produces more consistent and coherent responses.
  /// The number of bits used for context_size (2^n), default is 13 (8192)
  context_size: u32,
}

impl Default for LocalOptions {
  fn default() -> Self {
    Self {
      context_size: 13,
      model_arch: Some("llama".to_string()),
      model_path: Default::default(),
    }
  }
}

pub struct LocalClient {
  model_client: Box<dyn Model>,
  #[allow(dead_code)]
  options: Options<LocalOptions>,
}

impl From<&Options<LocalOptions>> for ModelParameters {
  fn from(value: &Options<LocalOptions>) -> Self {
    Self {
      context_size: 2_usize.pow(value.custom.context_size),
      ..Default::default()
    }
  }
}

// https://github.com/rustformers/llm/blob/main/crates/llm-base/src/samplers.rs#L241
impl BuiltinOptions {
  fn sampler_options(&self) -> Vec<String> {
    let mut options = vec![];

    if let Some(temperature) = self.temperature {
      options.push(format!("temperature:{}", temperature))
    }

    if let Some(top_p) = self.top_p {
      options.push(format!("topp:p={}", top_p))
    }

    if let Some(top_k) = self.top_k {
      options.push(format!("topk:k={}", top_k))
    }

    options
  }
}

#[async_trait]
impl Client for LocalClient {
  type Opt<'a> = LocalOptions;

  fn new_with_options(options: Options<Self::Opt<'_>>) -> Result<Self, ClientCreationError> {
    let model_arch = options
      .custom
      .model_arch
      .clone()
      .ok_or(ClientCreationError::FieldRequiredError(anyhow!(
        "Model arch is empty"
      )))?
      .parse::<ModelArchitecture>()
      .map_err(|e| ClientCreationError::InnerError(Box::new(e)))?;

    let model_client: Box<dyn Model> = llm::load_dynamic(
      Some(model_arch),
      &options
        .custom
        .model_path
        .clone()
        .ok_or(ClientCreationError::FieldRequiredError(anyhow!(
          "Model path is empty"
        )))?,
      TokenizerSource::Embedded,
      (&options).into(),
      load_progress_callback_stdout,
    )
    .map_err(|e| ClientCreationError::InnerError(Box::new(e)))?;

    info!("load model ok");

    Ok(Self {
      options,
      model_client,
    })
  }

  async fn chat_complete(
    &self,
    prompt: crate::model::Prompt,
    opt: BuiltinOptions,
  ) -> Result<Output, ExecutorError> {
    // https://github.com/rustformers/llm/blob/main/binaries/llm-test/src/inference.rs#L23
    let session = &mut self.model_client.start_session(Default::default());
    let mut output = String::new();
    let prompt = prompt.to_string();

    let sampler = llm::samplers::build_sampler(20000, &[], &opt.sampler_options())
      .map_err(|err| ExecutorError::InnerError(Box::new(err)))?;

    let request = InferenceRequest {
      prompt: llm::Prompt::Text(prompt.as_str()),
      parameters: &InferenceParameters { sampler },
      play_back_previous_tokens: false,
      maximum_token_count: None,
    };

    info!("request {:?}", request);

    session
      .infer::<Infallible>(
        self.model_client.as_ref(),
        &mut rand::thread_rng(),
        &request,
        // OutputRequest
        &mut Default::default(),
        |t| match t {
          llm::InferenceResponse::PromptToken(t) | llm::InferenceResponse::InferredToken(t) => {
            print!("\x1b[31m{t}\x1b[0m");
            let Ok(_) = std::io::stdout().flush() else {
              return Ok(llm::InferenceFeedback::Halt);
            };
            output += &t;
            Ok(llm::InferenceFeedback::Continue)
          }
          _ => Ok(llm::InferenceFeedback::Continue),
        },
      )
      .map_err(|err| ExecutorError::InnerError(Box::new(err)))?;

    info!("output {}", output);

    Ok(Output::new(output))
  }
}

fn load_progress_callback_stdout(progress: LoadProgress) {
  match progress {
    LoadProgress::HyperparametersLoaded => {
      debug!("Loaded hyperparameters")
    }
    LoadProgress::LoraApplied { name, source } => {
      debug!("Apply lora {} {:?}", name, source)
    }
    LoadProgress::ContextSize { bytes } => {
      debug!(
        "ggml ctx size = {:.2} MB\n",
        bytes as f64 / (1024.0 * 1024.0)
      )
    }
    LoadProgress::TensorLoaded {
      current_tensor,
      tensor_count,
      ..
    } => {
      let current_tensor = current_tensor + 1;
      if current_tensor % 8 == 0 {
        info!("Loaded tensor {current_tensor}/{tensor_count}");
      }
    }
    LoadProgress::Loaded {
      file_size: byte_size,
      tensor_count,
    } => {
      info!(
        "Loading of model complete, Model size = {:.2} MB / num tensors = {}",
        byte_size as f64 / 1024.0 / 1024.0,
        tensor_count
      );
    }
  };
}

#[cfg(test)]
mod tests {
  use super::*;

  #[test]
  fn client() {
    let client_error = LocalClient::new().is_err();
    assert!(client_error)
  }
}
