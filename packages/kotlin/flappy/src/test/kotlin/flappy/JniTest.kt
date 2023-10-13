package flappy

import com.pleisto.FlappyJniSandbox
import com.pleisto.FlappyJniSandboxInput
import kotlin.test.Test
import kotlin.test.assertEquals

class JniTest {

  @Test
  fun codeInterpreter() {
    val sandbox = FlappyJniSandbox()
    val result = sandbox.evalPythonCode(FlappyJniSandboxInput("""print("hello world")""")).get()
    assertEquals(result.stderr, "")
    assertEquals(result.stdout, "hello world\n")
  }

}
