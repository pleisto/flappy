package com.pleisto;

import lombok.NonNull;
import lombok.ToString;

@ToString
public class SandboxResult {
    public final String stdout;
    public final String stderr;

    public SandboxResult(@NonNull String stdout, @NonNull String stderr) {
        this.stdout = stdout;
        this.stderr = stderr;
    }
}
