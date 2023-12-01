package com.pleisto.task;

/**
 * Copy from https://github.com/deviceplug/jni-utils-rs/blob/master/java/src/main/java/io/github/gedgygedgy/rust
 *
 * Represents the result of polling an async future.
 * <p>
 * If a future result is available, the future should return an instance of
 * this interface which returns the result from the {@link get} method.
 * If no result is available, the future should return {@code null} (NOT a
 * {@link PollResult} whose {@link get} returns {@code null}.) A
 * non-{@code null} {@link PollResult} indicates that the future has completed.
 */
public interface PollResult<T> {
  /**
   * Gets the future result. This can be anything, including {@code null}.
   *
   * @return The future result.
   */
  public T get();
}
