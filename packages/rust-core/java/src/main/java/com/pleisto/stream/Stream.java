package com.pleisto.stream;

import com.pleisto.task.PollResult;
import com.pleisto.task.Waker;

/**
 * Copy from https://github.com/deviceplug/jni-utils-rs/blob/master/java/src/main/java/io/github/gedgygedgy/rust
 *
 * Interface for allowing Rust code to interact with Java code in an
 * asynchronous manner. The intention of this interface is for asynchronous
 * Rust code to directly call Java code that returns a {@link Stream}, and
 * then poll the {@link Stream} using a {@code jni_utils::stream::JStream}.
 * In this way, the complexities of interacting with asynchronous Java code can
 * be abstracted into a simple {@code jni_utils::stream::JStream} that Rust
 * code can {@code await} on.
 * <p>
 * In general, you will probably want to use {@link QueueStream} for most use
 * cases.
 */
public interface Stream<T> {
    /**
     * Polls the {@link Stream} for a new item. You generally won't need to
     * call this directly, as {@code jni_utils::future::JFuture} takes care of
     * this for you.
     * <p>
     * If no item is available, and it is not known whether or not more items
     * will be available in the future, this method should return {@code null}.
     * If an item is available, this method should return a {@link PollResult}
     * whose {@link PollResult#get} returns a non-{@code null}
     * {@link StreamPoll}. If no item is available, and no items will ever be
     * available, this method should return a {@link PollResult} whose
     * {@link PollResult#get} returns {@code null}.
     *
     * @param waker Waker to wake with when the {@link Stream} has a result.
     * @return The next item.
     */
    PollResult<StreamPoll<T>> pollNext(Waker waker);
}
