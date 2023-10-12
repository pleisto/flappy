package com.pleisto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicReference;

/**
 * NativeObject is the base-class of all OpenDAL classes that have
 * a pointer to a native object.
 *
 * <p>
 * NativeObject has the {@link NativeObject#close()} method, which frees its associated
 * native object.
 *
 * <p>
 * This function should be called manually, or even better, called implicitly using a
 * <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">try-with-resources</a>
 * statement, when you are finished with the object. It is no longer called automatically
 * during the regular Java GC process via {@link NativeObject#finalize()}.
 *
 * <p>
 * <b>Explanatory note</b>
 *
 * <p>
 * When or if the Garbage Collector calls {@link Object#finalize()}
 * depends on the JVM implementation and system conditions, which the programmer
 * cannot control. In addition, the GC cannot see through the native reference
 * long member variable (which is the pointer value to the native object),
 * and cannot know what other resources depend on it.
 *
 * <p>
 * Finalization is deprecated and subject to removal in a future release.
 * The use of finalization can lead to problems with security, performance,
 * and reliability. See <a href="https://openjdk.org/jeps/421">JEP 421</a>
 * for discussion and alternatives.
 */
public abstract class NativeObject implements AutoCloseable {

    private enum LibraryState {
        NOT_LOADED,
        LOADING,
        LOADED
    }

    private static final AtomicReference<LibraryState> libraryLoaded = new AtomicReference<>(LibraryState.NOT_LOADED);

    static {
        NativeObject.loadLibrary();
    }

    public static void loadLibrary() {
        if (libraryLoaded.get() == LibraryState.LOADED) {
            return;
        }

        if (libraryLoaded.compareAndSet(LibraryState.NOT_LOADED, LibraryState.LOADING)) {
            try {
                doLoadLibrary();
            } catch (IOException e) {
                libraryLoaded.set(LibraryState.NOT_LOADED);
                throw new UncheckedIOException("Unable to load the OpenDAL shared library", e);
            }
            libraryLoaded.set(LibraryState.LOADED);
            return;
        }

        while (libraryLoaded.get() == LibraryState.LOADING) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignore) {
            }
        }
    }

    private static void doLoadLibrary() throws IOException {
        try {
            // try dynamic library - the search path can be configured via "-Djava.library.path"
            System.loadLibrary("opendal_java");
            return;
        } catch (UnsatisfiedLinkError ignore) {
            // ignore - try from classpath
        }

        doLoadBundledLibrary();
    }

    private static void doLoadBundledLibrary() throws IOException {
        final String libraryPath = bundledLibraryPath();
        try (final InputStream is = NativeObject.class.getResourceAsStream(libraryPath)) {
            if (is == null) {
                throw new IOException("cannot find " + libraryPath);
            }
            final int dot = libraryPath.indexOf('.');
            final File tmpFile = File.createTempFile(libraryPath.substring(0, dot), libraryPath.substring(dot));
            tmpFile.deleteOnExit();
            Files.copy(is, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.load(tmpFile.getAbsolutePath());
        }
    }

    private static String bundledLibraryPath() {
        final String classifier = Environment.getClassifier();
        final String libraryName = System.mapLibraryName("opendal_java");
        return "/native/" + classifier + "/" + libraryName;
    }

    /**
     * An immutable reference to the value of the underneath pointer pointing
     * to some underlying native OpenDAL object.
     */
    protected final long nativeHandle;

    protected NativeObject(long nativeHandle) {
        this.nativeHandle = nativeHandle;
    }

    @Override
    public void close() {
        disposeInternal(nativeHandle);
    }

    /**
     * Deletes underlying native object pointer.
     *
     * @param handle to the native object pointer
     */
    protected abstract void disposeInternal(long handle);
}
