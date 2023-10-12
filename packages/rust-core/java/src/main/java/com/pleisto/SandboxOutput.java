package com.pleisto;

import lombok.NonNull;
import lombok.ToString;

@ToString
public class SandboxOutput {
    public final String stdout;
    public final String stderr;

    public SandboxOutput(@NonNull String stdout, @NonNull String stderr) {
        this.stdout = stdout;
        this.stderr = stderr;
    }
}
