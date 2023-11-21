use strum_macros::EnumDiscriminants;

#[derive(EnumDiscriminants)]
#[strum_discriminants(vis(pub))]
pub enum BuiltinOption {
  MaxTokens(f64),
  Temperature(f32),
  TopP(f32),
}

pub trait OptionInitial: Sized {
  fn initialize() -> Vec<Self> {
    Vec::new()
  }
}

impl OptionInitial for BuiltinOption {}

pub struct AllOptions<T: OptionInitial> {
  pub(crate) builtin: BuiltinOptions,
  pub(crate) custom: CustomOptions<T>,
}

#[derive(Default)]
pub struct BuiltinOptions(Vec<BuiltinOption>);

impl BuiltinOptions {
  pub fn get(&self, t: BuiltinOptionDiscriminants) -> Option<&BuiltinOption> {
    self
      .0
      .iter()
      .find(|o| BuiltinOptionDiscriminants::from(*o) == t)
  }
}
pub struct CustomOptions<T: OptionInitial>(Vec<T>);

impl<T: OptionInitial> CustomOptions<T> {
  pub fn get<F>(&self, f: F) -> Option<&T>
  where
    F: Fn(&&T) -> bool,
  {
    self.0.iter().find(f)
  }
}

impl<T: OptionInitial> Default for AllOptions<T> {
  fn default() -> Self {
    AllOptions {
      builtin: BuiltinOptions(BuiltinOption::initialize()),
      custom: CustomOptions(T::initialize()),
    }
  }
}

#[derive(Default)]
pub struct Options<T: OptionInitial> {
  opts: AllOptions<T>,
}

impl<T> Options<T>
where
  T: OptionInitial,
{
  pub fn new() -> Self {
    Self {
      opts: AllOptions::<T>::default(),
    }
  }

  pub fn size(self) -> usize {
    self.opts.builtin.0.len() + self.opts.custom.0.len()
  }

  pub fn add_builtin(&mut self, value: BuiltinOption) {
    self.opts.builtin.0.push(value)
  }

  pub fn add_custom(&mut self, value: T) {
    self.opts.custom.0.push(value)
  }

  pub fn get_custom<F>(&self, f: F) -> Option<&T>
  where
    F: Fn(&&T) -> bool,
  {
    self.opts.custom.get(f)
  }

  pub fn get_customs<F>(&self, fs: Vec<F>) -> Vec<Option<&T>>
  where
    F: Fn(&&T) -> bool,
  {
    fs.iter().map(|f| self.get_custom(f)).collect()
  }

  pub fn get_builtin(&self, t: BuiltinOptionDiscriminants) -> Option<&BuiltinOption> {
    self.opts.builtin.get(t)
  }
}

#[cfg(test)]
mod tests {

  use super::*;
  enum Foo {
    Bar,
  }
  impl OptionInitial for Foo {
    fn initialize() -> Vec<Self> {
      vec![Foo::Bar]
    }
  }

  #[test]
  fn default() {
    let v: Options<Foo> = Options::new();

    assert_eq!(v.size(), 1)
  }

  #[test]
  fn get_builtin_test() {
    let mut v = Options::<Foo>::new();
    v.add_builtin(BuiltinOption::MaxTokens(1.0));
    let data = v.get_builtin(BuiltinOptionDiscriminants::MaxTokens);

    assert_eq!(data.is_some(), true);
  }
}
