package com.pleisto.thread;

/**
 * Copy from https://github.com/deviceplug/jni-utils-rs/blob/master/java/src/main/java/io/github/gedgygedgy/rust
 */
public class LocalThreadChecker {
    private final Thread origin;

    public LocalThreadChecker(boolean local) {
        this.origin = local ? Thread.currentThread() : null;
    }

    public void check() {
        if (this.origin != null && this.origin != Thread.currentThread()) {
            throw new LocalThreadException();
        }
    }
}
