package com.pleisto;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;

public class FlappyJniSandbox extends FlappyJniLoader implements AutoCloseable {
    String cachePath;

    public FlappyJniSandbox(String cachePath) throws IOException {
        this.cachePath = cachePath == null || cachePath.isEmpty()
                ? Files.createTempDirectory("flappyJniSandbox").toFile().getAbsolutePath()
                : cachePath;
    }

    public FlappyJniSandbox() throws IOException {
        this(null);
    }

    @Override
    public void close() {}

    public static class FlappyJniSandboxInput {
        public final String code;

        public final boolean network;

        public final Map<String, String> envs;

        public FlappyJniSandboxInput(@NonNull String code, boolean network, @NonNull Map<String, String> envs) {
            this.code = code;
            this.network = network;
            this.envs = envs;
        }

        public FlappyJniSandboxInput(@NonNull String code, boolean network) {
            this(code, network, new HashMap<>());
        }

        public FlappyJniSandboxInput(@NonNull String code) {
            this(code, false);
        }
    }

    private static native long nativeEvalPythonCode(
            String code, boolean network, Map<String, String> envs, String cache_path);

    private static native long nativePrepareSandbox(String cache_path);

    public static native String ping();

    public CompletableFuture<Void> nativePrepareSandbox() {
        final long requestId = nativePrepareSandbox(this.cachePath);
        return FlappyAsyncRegistry.take(requestId);
    }

    public CompletableFuture<FlappyJniSandboxResult> evalPythonCode(FlappyJniSandboxInput input) {
        final long requestId = nativeEvalPythonCode(input.code, input.network, input.envs, this.cachePath);
        return FlappyAsyncRegistry.take(requestId);
    }
}
