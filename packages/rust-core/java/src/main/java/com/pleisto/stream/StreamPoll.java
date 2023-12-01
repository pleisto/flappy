package com.pleisto.stream;

/**
 * Copy from https://github.com/deviceplug/jni-utils-rs/blob/master/java/src/main/java/io/github/gedgygedgy/rust
 *
 * Represents the result of polling an async stream.
 * <p>
 * See {@link Stream#pollNext} for a description of how to use this interface.
 */
public interface StreamPoll<T> {
    /**
     * Gets the stream item. This can be anything, including {@code null}.
     *
     * @return The stream item.
     */
    T get();
}
