package flappy

import com.pleisto.FlappyJniSandbox
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails


class JniTest {

  @Test
  fun codeInterpreterSuccess() {
    val sandbox = FlappyJniSandbox()
    val result = sandbox.evalPythonCode(FlappyJniSandbox.FlappyJniSandboxInput("""print("hello world")""")).get()
    assertEquals(result.stderr, "")
    assertEquals(result.stdout, "hello world\n")
  }

  @Test
  fun codeInterpreterDumb() {
    val sandbox = FlappyJniSandbox()
    val result = sandbox.evalPythonCode(FlappyJniSandbox.FlappyJniSandboxInput("""1+1""")).get()
    assertEquals(result.stderr, "")
    assertEquals(result.stdout, "")
  }

  @Test
  fun codeInterpreterFail() {
    val sandbox = FlappyJniSandbox()
    assertFails {
      sandbox.evalPythonCode(FlappyJniSandbox.FlappyJniSandboxInput("""foo""")).get()
    }
  }
}
