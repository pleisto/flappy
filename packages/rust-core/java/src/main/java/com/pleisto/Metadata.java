package com.pleisto;

/**
 * Metadata carries all metadata associated with a path.
 */
public class Metadata extends NativeObject {
    protected Metadata(long nativeHandle) {
        super(nativeHandle);
    }

    public boolean isFile() {
        return isFile(nativeHandle);
    }

    public long getContentLength() {
        return getContentLength(nativeHandle);
    }

    @Override
    protected native void disposeInternal(long handle);

    private static native boolean isFile(long nativeHandle);

    private static native long getContentLength(long nativeHandle);
}
