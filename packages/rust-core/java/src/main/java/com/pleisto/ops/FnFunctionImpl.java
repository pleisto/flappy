package com.pleisto.ops;

/**
 * Copy from https://github.com/deviceplug/jni-utils-rs/blob/master/java/src/main/java/io/github/gedgygedgy/rust
 */
final class FnFunctionImpl<T, R> implements FnFunction<T, R> {
    private final FnAdapter<FnFunctionImpl<T, R>, T, Void, R> adapter;

    private FnFunctionImpl(FnAdapter<FnFunctionImpl<T, R>, T, Void, R> adapter) {
        this.adapter = adapter;
    }

    @Override
    public R apply(T t) {
        return this.adapter.call(this, t, null);
    }

    @Override
    public void close() {
        this.adapter.close();
    }
}
