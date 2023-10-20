package com.pleisto;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

public class FlappyJniSandbox extends FlappyJniLoader {
    String cachePath;

    FlappyJniSandbox(String cachePath) throws IOException {
        this.cachePath = cachePath == null || cachePath.isEmpty()
                ? Files.createTempDirectory("flappyJniSandbox").toFile().getAbsolutePath()
                : cachePath;
    }

    FlappyJniSandbox() throws IOException {
        this(null);
    }

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

    private enum AsyncRegistry {
        INSTANCE;

        private final Map<Long, CompletableFuture<?>> registry = new ConcurrentHashMap<>();

        @SuppressWarnings("unused")
        private static long requestId() {
            final CompletableFuture<?> f = new CompletableFuture<>();
            while (true) {
                final long requestId = Math.abs(UUID.randomUUID().getLeastSignificantBits());
                final CompletableFuture<?> prev = INSTANCE.registry.putIfAbsent(requestId, f);
                if (prev == null) {
                    return requestId;
                }
            }
        }

        private static CompletableFuture<?> get(long requestId) {
            return INSTANCE.registry.get(requestId);
        }

        @SuppressWarnings("unchecked")
        private static <T> CompletableFuture<T> take(long requestId) {
            final CompletableFuture<?> f = get(requestId);
            if (f != null) {
                f.whenComplete((r, e) -> INSTANCE.registry.remove(requestId));
            }
            return (CompletableFuture<T>) f;
        }
    }

    private static native long nativeEvalPythonCode(
            String code, boolean network, Map<String, String> envs, String cache_path);

    private static native long nativePrepareSandbox(String cache_path);

    public static native String ping();

    public CompletableFuture<Void> nativePrepareSandbox() {
        final long requestId = nativePrepareSandbox(this.cachePath);
        return (CompletableFuture<Void>) AsyncRegistry.get(requestId);
    }

    public CompletableFuture<FlappyJniSandboxResult> evalPythonCode(FlappyJniSandboxInput input) {
        final long requestId = nativeEvalPythonCode(input.code, input.network, input.envs, this.cachePath);
        return (CompletableFuture<FlappyJniSandboxResult>) AsyncRegistry.get(requestId);
    }
}
