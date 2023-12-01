package com.pleisto.ops;

/**
 * Copy from https://github.com/deviceplug/jni-utils-rs/blob/master/java/src/main/java/io/github/gedgygedgy/rust
 */
final class FnRunnableImpl implements FnRunnable {
    private final FnAdapter<FnRunnableImpl, Void, Void, Void> adapter;

    private FnRunnableImpl(FnAdapter<FnRunnableImpl, Void, Void, Void> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void run() {
        this.adapter.call(this, null, null);
    }

    @Override
    public void close() {
        this.adapter.close();
    }
}
