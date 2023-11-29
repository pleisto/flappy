use std::{convert::Infallible, path::PathBuf, time::Instant};

use anyhow::anyhow;
use async_trait::async_trait;
use derive_builder::Builder;
use llm::{
  InferenceFeedback, InferenceParameters, InferenceRequest, InferenceResponse, LoadProgress, Model,
  ModelArchitecture, ModelParameters, TokenizerSource,
};
use spinoff::{spinners::Dots2, Spinner};
use tracing::{debug, info};

use crate::{
  client::Client,
  error::{ClientCreationError, ExecuteError},
  options::{BuiltinOptions, Options},
  output::{stream::StreamItem, ImmediateOutput, StreamOutput},
  prompt::Prompt,
};

#[derive(Builder)]
#[builder(default)]
pub struct LocalOptions {
  model_arch: Option<String>,
  model_path: Option<PathBuf>,
  tokenizer_path: Option<PathBuf>,
  /// The context size ("memory") the model should use when evaluating a prompt. A larger context
  /// consumes more resources, but produces more consistent and coherent responses.
  /// The number of bits used for context_size (2^n), default is 13 (8192)
  context_size: u32,
}

impl Default for LocalOptions {
  fn default() -> Self {
    Self {
      context_size: 13,
      tokenizer_path: None,
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

impl LocalClient {
  fn inference(
    &self,
    prompt: Prompt,
    opt: BuiltinOptions,
    callback: impl FnMut(InferenceResponse) -> Result<InferenceFeedback, Infallible>,
  ) -> Result<(), ExecuteError> {
    // https://github.com/rustformers/llm/blob/main/binaries/llm-test/src/inference.rs#L23
    let prompt = prompt.to_string();
    let sampler = llm::samplers::build_sampler(0, &[], &opt.sampler_options())
      .map_err(|err| ExecuteError::Inner(err.into()))?;

    let parameters = &InferenceParameters { sampler };

    let request = InferenceRequest {
      prompt: llm::Prompt::Text(prompt.as_str()),
      parameters,
      play_back_previous_tokens: false,
      maximum_token_count: None,
    };

    info!("request {:?}", request);

    let session = &mut self.model_client.start_session(Default::default());

    let result = session
      .infer::<Infallible>(
        self.model_client.as_ref(),
        &mut rand::thread_rng(),
        &request,
        // OutputRequest
        &mut Default::default(),
        callback,
      )
      .map_err(|err| ExecuteError::Inner(err.into()))?;

    info!("result {}", result);

    Ok(())
  }
}

#[async_trait]
impl Client for LocalClient {
  type Opt<'a> = LocalOptions;
  type Output<'a> = String;
  type StreamSegment<'a> = String;

  fn new_with_options(options: Options<Self::Opt<'_>>) -> Result<Self, ClientCreationError> {
    let model_arch = options
      .custom
      .model_arch
      .clone()
      .ok_or(ClientCreationError::FieldRequired(anyhow!(
        "Model arch is empty"
      )))?
      .parse::<ModelArchitecture>()
      .map_err(|e| ClientCreationError::Inner(e.into()))?;

    let tokenizer_source = if let Some(path) = &options.custom.tokenizer_path {
      TokenizerSource::HuggingFaceTokenizerFile(path.clone())
    } else {
      TokenizerSource::Embedded
    };

    let sp = Some(Spinner::new(Dots2, "Loading model...", None));
    let now = Instant::now();
    let prev_load_time = now;

    let model_client: Box<dyn Model> = llm::load_dynamic(
      Some(model_arch),
      &options
        .custom
        .model_path
        .clone()
        .ok_or(ClientCreationError::FieldRequired(anyhow!(
          "Model path is empty"
        )))?,
      tokenizer_source,
      (&options).into(),
      load_progress_callback(sp, now, prev_load_time),
    )
    .map_err(|e| ClientCreationError::Inner(e.into()))?;

    info!("load model ok");

    Ok(Self {
      options,
      model_client,
    })
  }

  async fn chat_complete(
    &self,
    prompt: crate::prompt::Prompt,
    opt: BuiltinOptions,
  ) -> Result<ImmediateOutput<String>, ExecuteError> {
    let mut output = String::new();
    self.inference(prompt, opt, |t| match t {
      llm::InferenceResponse::PromptToken(t) | llm::InferenceResponse::InferredToken(t) => {
        // print!("\x1b[31m{t}\x1b[0m");
        // let Ok(_) = std::io::stdout().flush() else {
        //   return Ok(llm::InferenceFeedback::Halt);
        // };
        output.push_str(&t);
        Ok(llm::InferenceFeedback::Continue)
      }
      _ => Ok(llm::InferenceFeedback::Continue),
    })?;

    info!("output {}", output);

    Ok(ImmediateOutput::new(output))
  }

  async fn chat_complete_stream(
    &self,
    prompt: Prompt,
    opt: BuiltinOptions,
  ) -> Result<StreamOutput<String>, ExecuteError> {
    let (sender, output) = StreamOutput::<String>::new();

    self.inference(prompt, opt, |t| match t {
      llm::InferenceResponse::PromptToken(t) | llm::InferenceResponse::InferredToken(t) => {
        match sender.send(StreamItem::Data(t)) {
          Ok(_) => Ok(llm::InferenceFeedback::Continue),
          Err(_) => Ok(llm::InferenceFeedback::Halt),
        }
      }
      _ => Ok(llm::InferenceFeedback::Continue),
    })?;

    Ok(output)
  }
}

fn load_progress_callback(
  mut sp: Option<Spinner>,
  now: Instant,
  mut prev_load_time: Instant,
) -> impl FnMut(LoadProgress) {
  move |progress| match progress {
    LoadProgress::HyperparametersLoaded => {
      if let Some(sp) = sp.as_mut() {
        sp.update_text("Loaded hyperparameters")
      };
    }
    LoadProgress::ContextSize { bytes } => debug!(
      "ggml ctx size = {:.2} MB\n",
      bytes as f64 / (1024.0 * 1024.0)
    ),
    LoadProgress::TensorLoaded {
      current_tensor,
      tensor_count,
      ..
    } => {
      if prev_load_time.elapsed().as_millis() > 500 {
        // We don't want to re-render this on every message, as that causes the
        // spinner to constantly reset and not look like it's spinning (and
        // it's obviously wasteful).
        if let Some(sp) = sp.as_mut() {
          sp.update_text(format!(
            "Loaded tensor {}/{}",
            current_tensor + 1,
            tensor_count
          ));
        };
        prev_load_time = std::time::Instant::now();
      }
    }
    LoadProgress::LoraApplied { name, source } => {
      if let Some(sp) = sp.as_mut() {
        sp.update_text(format!(
          "Applied LoRA: {} from '{}'",
          name,
          source.file_name().unwrap().to_str().unwrap()
        ));
      };
    }
    LoadProgress::Loaded {
      file_size,
      tensor_count,
    } => {
      if let Some(mut sp) = sp.take() {
        sp.success(&format!(
          "Loading of model complete, Model size = {:.2} MB / num tensors = {} after {}ms",
          file_size as f64 / 1024.0 / 1024.0,
          tensor_count,
          now.elapsed().as_millis()
        ));
      };
    }
  }
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
