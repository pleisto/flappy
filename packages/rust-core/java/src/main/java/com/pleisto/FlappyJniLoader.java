package com.pleisto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicReference;

abstract class FlappyJniLoader {

    private enum LibraryState {
        NOT_LOADED,
        LOADING,
        LOADED
    }

    private static final AtomicReference<LibraryState> libraryLoaded = new AtomicReference<>(LibraryState.NOT_LOADED);

    static {
        FlappyJniLoader.loadLibrary();
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
                throw new UncheckedIOException("Unable to load the shared library", e);
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
            System.loadLibrary("flappy_java_bindings");
            return;
        } catch (UnsatisfiedLinkError ignore) {
            // ignore - try from classpath
        }

        doLoadBundledLibrary();
    }

    private static void doLoadBundledLibrary() throws IOException {
        final String libraryPath = bundledLibraryPath();
        try (final InputStream is = FlappyJniLoader.class.getResourceAsStream(libraryPath)) {
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
        final String libraryName = System.mapLibraryName("flappy_java_bindings");
        return "/native/" + classifier + "/" + libraryName;
    }
}
