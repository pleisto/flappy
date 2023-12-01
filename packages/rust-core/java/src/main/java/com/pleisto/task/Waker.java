package com.pleisto.task;

import com.pleisto.ops.FnRunnable;

import java.io.Closeable;

/**
 * Copy from https://github.com/deviceplug/jni-utils-rs/blob/master/java/src/main/java/io/github/gedgygedgy/rust
 *
 * Wraps a {@code std::task::Waker} in a Java object.
 * <p>
 * Instances of this class cannot be obtained directly from Java. Instead, call
 * {@code jni_utils::task::waker()} from Rust code to obtain an instance of
 * this class. (This generally shouldn't be necessary, since
 * {@code jni_utils::future::JFuture} and {@code jni_utils::stream::JStream}
 * take care of this for you.)
 */
public final class Waker implements Closeable {
  private final FnRunnable wakeRunnable;

  private Waker(FnRunnable wakeRunnable) {
    this.wakeRunnable = wakeRunnable;
  }

  /**
   * Wakes the {@code std::task::Waker} associated with this object.
   * <p>
   * If the {@code std::task::Waker} has already been woken, this method
   * does nothing.
   */
  public void wake() {
    this.wakeRunnable.run();
  }

  /**
   * Frees the {@code std::task::Waker} associated with this object.
   */
  @Override
  public void close() {
    this.wakeRunnable.close();
  }
}
