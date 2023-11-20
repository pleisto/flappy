pub enum BuiltinOption {
  MaxTokens(f64),
  Temperature(f64),
  TopP(f64),
}

pub enum AllOption<T> {
  Builtin(BuiltinOption),
  Custom(T),
}

#[derive(Default)]
pub struct Options<T> {
  opts: Vec<AllOption<T>>,
}

impl<T> Options<T> {
  pub fn new() -> Self {
    Self { opts: Vec::new() }
  }

  pub fn add(&mut self, opt: AllOption<T>) {
    self.opts.push(opt);
  }
}

#[cfg(test)]
mod tests {

  use super::*;

  #[test]
  fn default() {
    enum Foo {}

    let v: Options<Foo> = Options::new();

    assert_eq!(v.opts.len(), 0)
  }
}
