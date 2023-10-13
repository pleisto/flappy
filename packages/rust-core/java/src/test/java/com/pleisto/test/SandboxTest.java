package com.pleisto.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.pleisto.FlappyJniSandbox;
import com.pleisto.FlappyJniSandboxInput;
import com.pleisto.FlappyJniSandboxResult;

import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

public class SandboxTest {

    @Test
    public void ping() {
        assertThat(FlappyJniSandbox.ping()).isEqualTo("pong");
    }

    @Test
    public void evalFail() throws InterruptedException {
        final FlappyJniSandbox op = new FlappyJniSandbox();
        try {
            FlappyJniSandboxResult result = op.evalPythonCode(new FlappyJniSandboxInput("foobar")).get();
            assertThat(false).isTrue();
        } catch (ExecutionException e) {
            assertThat(true).isTrue();
        }
    }

    @Test
    public void evalSuccess() throws ExecutionException, InterruptedException {
        final FlappyJniSandbox op = new FlappyJniSandbox();
        String code = "print('hello world')";
        FlappyJniSandboxResult result = op.evalPythonCode(new FlappyJniSandboxInput(code)).get();
        assertThat(result).isNotNull();
        System.out.println(result);
        assertThat(result.stdout).isEqualTo("hello world\n");
    }

  @Test
  public void echo() {
    assertThat(FlappyJniSandbox.echo("foo", true, "bar")).isEqualTo("code: foo, network: true, cache_path: Some(\"bar\")");
    assertThat(FlappyJniSandbox.echo("foo", false, "")).isEqualTo("code: foo, network: false, cache_path: None");
  }
}
