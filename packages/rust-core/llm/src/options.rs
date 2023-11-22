use derive_builder::Builder;

#[derive(Default, Builder, Clone)]
#[builder(default)]
pub struct BuiltinOptions {
  pub(crate) max_token: Option<f64>,

  /// The temperature to use for token selection. Higher values result in more random output.
  pub(crate) temperature: Option<f32>,

  /// The cumulative probability threshold for token selection.
  pub(crate) top_p: Option<f32>,

  /// The maximum number of tokens to consider for each step of generation.
  pub(crate) top_k: Option<f32>,
}

impl From<()> for BuiltinOptions {
  fn from(_: ()) -> Self {
    Self::default()
  }
}

#[derive(Default, Builder)]
pub struct Options<T: Default> {
  #[allow(dead_code)]
  pub(crate) builtin: BuiltinOptions,
  pub(crate) custom: T,
}

impl<T: Default> Options<T> {
  pub fn new(builtin: BuiltinOptions, custom: T) -> Self {
    Self { builtin, custom }
  }
}

#[cfg(test)]
mod tests {
  use super::*;

  #[test]
  fn build() {
    let opt = BuiltinOptions::default();

    assert!(opt.top_p.is_none());

    let opt2 = BuiltinOptionsBuilder::default()
      .top_p(Some(1.0))
      .build()
      .unwrap();

    assert_eq!(opt2.top_p.unwrap(), 1.0);
  }
}
