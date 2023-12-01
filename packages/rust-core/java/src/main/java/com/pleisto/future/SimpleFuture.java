package com.pleisto.future;


import com.pleisto.task.PollResult;
import com.pleisto.task.Waker;

/**
 * Copy from https://github.com/deviceplug/jni-utils-rs/blob/master/java/src/main/java/io/github/gedgygedgy/rust
 *
 * Simple implementation of {@link Future} which can be woken with a result.
 * In general, methods which create a {@link SimpleFuture} should return it as
 * a {@link Future} to keep calling code from waking it.
 */
public class SimpleFuture<T> implements Future<T> {
    private Waker waker = null;
    private PollResult<T> result;
    private final Object lock = new Object();

    /**
     * Creates a new {@link SimpleFuture} object.
     */
    public SimpleFuture() {}

    @Override
    public PollResult<T> poll(Waker waker) {
        PollResult<T> result = null;
        Waker oldWaker = null;
        synchronized (this.lock) {
            if (this.result != null) {
                result = this.result;
            } else {
                oldWaker = this.waker;
                this.waker = waker;
            }
        }
        if (oldWaker != null) {
            oldWaker.close();
        }
        if (result != null) {
            waker.close();
        }
        return result;
    }

    private void wakeInternal(PollResult<T> result) {
        Waker waker = null;
        synchronized (this.lock) {
            assert this.result == null;
            this.result = result;
            waker = this.waker;
        }
        if (waker != null) {
            waker.wake();
        }
    }

    /**
     * Wakes the {@link SimpleFuture} with a result.
     *
     * @param result Result to wake with. This can be {@code null}.
     */
    public void wake(T result) {
        this.wakeInternal(() -> {
            return result;
        });
    }

    /**
     * Wakes the {@link SimpleFuture} with an exception. When code calls
     * {@link PollResult#get} on the resulting {@link PollResult}, a
     * {@link FutureException} will be thrown with the given exception as the
     * cause.
     *
     * @param result Exception to wake with.
     */
    public void wakeWithThrowable(Throwable result) {
        this.wakeInternal(() -> {
            throw new FutureException(result);
        });
    }
}
