package com.pleisto.future;

import com.pleisto.task.PollResult;
import com.pleisto.task.Waker;

/**
 * Copy from https://github.com/deviceplug/jni-utils-rs/blob/master/java/src/main/java/io/github/gedgygedgy/rust
 *
 * Interface for allowing Rust code to interact with Java code in an
 * asynchronous manner. The intention of this interface is for asynchronous
 * Rust code to directly call Java code that returns a {@link Future}, and
 * then poll the {@link Future} using a {@code jni_utils::future::JFuture}.
 * In this way, the complexities of interacting with asynchronous Java code can
 * be abstracted into a simple {@code jni_utils::future::JFuture} that Rust
 * code can {@code await} on.
 * <p>
 * In general, you will probably want to use {@link SimpleFuture} for most use
 * cases.
 */
public interface Future<T> {
    /**
     * Polls the {@link Future} for a result. You generally won't need to call
     * this directly, as {@code jni_utils::future::JFuture} takes care of this
     * for you.
     *
     * @param waker Waker to wake with when the {@link Future} has a result.
     * @return The future's result, or {@code null} if no result is available
     * yet.
     */
    PollResult<T> poll(Waker waker);
}
