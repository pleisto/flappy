use std::slice::Iter;

use strum_macros::EnumDiscriminants;

#[derive(EnumDiscriminants)]
#[strum_discriminants(vis(pub))]
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

  pub fn get_custom<F>(&self, f: F) -> Option<&T>
  where
    F: Fn(&T) -> bool,
  {
    {
      for o in self.opts.iter() {
        match o {
          AllOption::Custom(op) => {
            if f(op) {
              return Some(op);
            }
          }
          AllOption::Builtin(_) => (),
        }
      }
      None
    }
  }

  pub fn get_builtin<F>(&self, f: F) -> Option<&BuiltinOption>
  where
    F: Fn(&BuiltinOption) -> bool,
  {
    for o in self.opts.iter() {
      match o {
        AllOption::Builtin(op) => {
          if f(op) {
            return Some(op);
          }
        }
        AllOption::Custom(_) => (),
      }
    }
    None
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

  #[test]
  fn get_builtin() {
    enum Foo {}
    let mut v = Options::<Foo>::new();
    v.add(AllOption::Builtin(BuiltinOption::MaxTokens(1.0)));
    let data = v.get_builtin(|item| {
      BuiltinOptionDiscriminants::from(item) == BuiltinOptionDiscriminants::MaxTokens
    });

    assert_eq!(data.is_some(), true);
  }
}
