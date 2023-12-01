package com.pleisto.ops;

/**
 * Copy from https://github.com/deviceplug/jni-utils-rs/blob/master/java/src/main/java/io/github/gedgygedgy/rust
 */
final class FnBiFunctionImpl<T, U, R> implements FnBiFunction<T, U, R> {
    private final FnAdapter<FnBiFunctionImpl<T, U, R>, T, U, R> adapter;

    private FnBiFunctionImpl(FnAdapter<FnBiFunctionImpl<T, U, R>, T, U, R> adapter) {
        this.adapter = adapter;
    }

    @Override
    public R apply(T t, U u) {
        return this.adapter.call(this, t, u);
    }

    @Override
    public void close() {
        this.adapter.close();
    }
}
