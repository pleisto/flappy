package com.pleisto;

import lombok.NonNull;
import lombok.ToString;

@ToString
public class OperatorInfo {
    public final String scheme;
    public final String root;
    public final String name;
    public final Capability fullCapability;
    public final Capability nativeCapability;

    public OperatorInfo(
            @NonNull String scheme,
            @NonNull String root,
            @NonNull String name,
            @NonNull Capability fullCapability,
            @NonNull Capability nativeCapability) {
        this.scheme = scheme;
        this.root = root;
        this.name = name;
        this.fullCapability = fullCapability;
        this.nativeCapability = nativeCapability;
    }
}
