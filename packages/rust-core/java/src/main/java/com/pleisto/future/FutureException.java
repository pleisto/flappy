package com.pleisto.future;

/**
 * Copy from https://github.com/deviceplug/jni-utils-rs/blob/master/java/src/main/java/io/github/gedgygedgy/rust
 *
 * Exception class for {@link Future} implementations to throw from
 * {@link io.github.gedgygedgy.rust.task.PollResult#get} if the result of the
 * future is an exception. Implementations should set the real exception as the
 * cause of this exception.
 */
public class FutureException extends RuntimeException {
    public FutureException(Throwable cause) {
        super(cause);
    }
}
