package com.pleisto;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class FlappyJniSandboxInput {
  public final String code;

  public final boolean network;

  public final Map<String, String> envs;

  public final String cachePath;
  public FlappyJniSandboxInput(@NonNull String code, boolean network, @NonNull Map<String, String> envs, String cachePath) {
    this.code = code;
    this.network = network;
    this.envs = envs;
    this.cachePath = cachePath == null ? "" : cachePath;
  }

  public FlappyJniSandboxInput(@NonNull String code, boolean network, @NonNull Map<String, String> envs) {
    this(code, network, envs, null);
  }

  public FlappyJniSandboxInput(@NonNull String code, boolean network) {
    this(code, network, new HashMap<>());
  }

  public FlappyJniSandboxInput(@NonNull String code) {
    this(code, false);
  }
}
