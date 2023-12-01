package com.pleisto.thread;

/**
 * Copy from https://github.com/deviceplug/jni-utils-rs/blob/master/java/src/main/java/io/github/gedgygedgy/rust
 *
 * Thrown when a non-{@code Send} Rust object is accessed from outside its
 * origin thread.
 */
public class LocalThreadException extends IllegalStateException {
    /**
     * Creates a new exception.
     */
    public LocalThreadException() {}
}
