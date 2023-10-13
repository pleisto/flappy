package com.pleisto;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class FlappyJniSandbox extends FlappyJniLoader {

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

    public static native String ping();

    public CompletableFuture<FlappyJniSandboxResult> evalPythonCode(FlappyJniSandboxInput input) {
        final long requestId = nativeEvalPythonCode(input.code, input.network, input.envs, input.cachePath);
        return (CompletableFuture<FlappyJniSandboxResult>) AsyncRegistry.get(requestId);
    }
}
