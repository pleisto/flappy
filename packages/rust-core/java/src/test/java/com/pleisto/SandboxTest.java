package com.pleisto;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

public class SandboxTest {

    FlappyJniSandbox sandbox = new FlappyJniSandbox("tmp");

    public SandboxTest() throws IOException {}

    @Test
    public void prepare() throws ExecutionException, InterruptedException {
        sandbox.nativePrepareSandbox().get();
    }

    @Test
    public void ping() {
        assertThat(FlappyJniSandbox.ping()).isEqualTo("pong");
    }

    @Test
    public void evalFail() throws InterruptedException {
        try {
            FlappyJniSandboxResult result = sandbox.evalPythonCode(new FlappyJniSandbox.FlappyJniSandboxInput("foobar"))
                    .get();
            assertThat(false).isTrue();
        } catch (ExecutionException e) {
            assertThat(true).isTrue();
        }
    }

    @Test
    public void evalSuccess() throws ExecutionException, InterruptedException {
        String code = "print('hello world')";
        FlappyJniSandboxResult result = sandbox.evalPythonCode(new FlappyJniSandbox.FlappyJniSandboxInput(code))
                .get();
        assertThat(result).isNotNull();
        assertThat(result.stdout).isEqualTo("hello world\n");
        assertThat(result.stderr).isEqualTo("");
    }

    @Test
    public void evalDumb() throws ExecutionException, InterruptedException {
        String code = "1+1";
        FlappyJniSandboxResult result = sandbox.evalPythonCode(new FlappyJniSandbox.FlappyJniSandboxInput(code))
                .get();
        assertThat(result).isNotNull();
        assertThat(result.stdout).isEqualTo("");
        assertThat(result.stderr).isEqualTo("");
    }

    @Test
    public void echo() {
        assertThat(FlappyJniDummy.echo("foo", true, new HashMap<>(), "bar"))
                .isEqualTo("code: foo, network: true, envs: [], cache_path: Some(\"bar\")");
        assertThat(FlappyJniDummy.echo("foo", false, Collections.singletonMap("A", "32"), ""))
                .isEqualTo("code: foo, network: false, envs: [(\"A\", \"32\")], cache_path: None");
    }
}
