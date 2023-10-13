package com.pleisto.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.pleisto.FlappyJniSandbox;
import com.pleisto.FlappySandboxResult;

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
            FlappySandboxResult result = op.evalPythonCode("foobar").get();
            assertThat(false).isTrue();
        } catch (ExecutionException e) {
            assertThat(true).isTrue();
        }
    }

    @Test
    public void evalSuccess() throws ExecutionException, InterruptedException {
        final FlappyJniSandbox op = new FlappyJniSandbox();
        String code = "print('hello world')";
        FlappySandboxResult result = op.evalPythonCode(code).get();
        assertThat(result).isNotNull();
        System.out.println(result);
        assertThat(result.stdout).isEqualTo("hello world\n");
    }
}
