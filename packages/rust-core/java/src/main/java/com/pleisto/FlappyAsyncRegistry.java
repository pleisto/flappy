package com.pleisto;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public enum FlappyAsyncRegistry {
    INSTANCE;

    private final Map<Long, CompletableFuture<?>> registry = new ConcurrentHashMap<>();

    static long requestId() {
        final CompletableFuture<?> f = new CompletableFuture<>();
        while (true) {
            final long requestId = Math.abs(UUID.randomUUID().getLeastSignificantBits());
            final CompletableFuture<?> prev = INSTANCE.registry.putIfAbsent(requestId, f);
            if (prev == null) {
                return requestId;
            }
        }
    }

    static CompletableFuture<?> get(long requestId) {
        return INSTANCE.registry.get(requestId);
    }

    static <T> CompletableFuture<T> take(long requestId) {
        final CompletableFuture<?> f = get(requestId);
        if (f != null) {
            f.whenComplete((r, e) -> INSTANCE.registry.remove(requestId));
        }
        return (CompletableFuture<T>) f;
    }
}
