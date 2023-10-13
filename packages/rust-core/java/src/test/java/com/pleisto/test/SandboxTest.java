package com.pleisto.test;

import static org.assertj.core.api.Assertions.assertThat;
import com.pleisto.JniSandbox;
import com.pleisto.SandboxResult;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

public class SandboxTest {

    @Test
    public void ping() {
        assertThat(JniSandbox.ping()).isEqualTo("pong");
    }

    @Test
    public void evalFail() throws InterruptedException {
        final JniSandbox op = new JniSandbox();
        try {
            SandboxResult result = op.evalPythonCode("foobar").get();
            assertThat(false).isTrue();
        } catch (ExecutionException e) {
            assertThat(true).isTrue();
        }
    }

    @Test
    public void evalSuccess() throws ExecutionException, InterruptedException {
        final JniSandbox op = new JniSandbox();
        String code = "print('hello world')";
        SandboxResult result = op.evalPythonCode(code).get();
        assertThat(result).isNotNull();
        System.out.println(result);
        assertThat(result.stdout).isEqualTo("hello world\n");
    }
}
