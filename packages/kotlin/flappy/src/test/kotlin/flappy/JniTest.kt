package flappy

import com.pleisto.FlappyJniException
import flappy.features.FlappyCodeInterpreter
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class JniTest {
  @Test
  fun schema() {
    val sandboxFunction = FlappyCodeInterpreter()
    sandboxFunction.use {
      assertEquals(
        sandboxFunction.argsTypeSchemaPropertiesString,
        """{"type":"string","description":"Function arguments"}"""
      )

      assertEquals(
        sandboxFunction.returnTypeSchemaPropertiesString,
        """{"type":"string","description":"Function return type"}"""
      )

      assertEquals(
        sandboxFunction.definition().asJSON(),
        """{"name":"pythonSandbox","description":"An safe sandbox that only support the built-in library. The execution time is limited to 120 seconds. The task is to define a function named \"main\" that doesn't take any parameters. The output should be a String. Network access is disabled","parameters":{"properties":{"args":{"type":"string","description":"Function arguments"},"returnType":{"type":"string","description":"Function return type"}},"type":"object"}}"""
      )
    }
  }


  @Test
  fun codeInterpreterSuccess() {
    val sandbox = FlappySandbox()
    sandbox.use {
      runBlocking {
        val result = it.evalPython(FlappySandbox.Input("""print("hello world")"""))
        assertEquals(result.stderr, "")
        assertEquals(result.stdout, "hello world\n")
      }
    }
  }

  @Test
  fun codeInterpreterDumb() {
    val sandbox = FlappySandbox()
    sandbox.use {
      runBlocking {
        val result = it.evalPython(FlappySandbox.Input("""1+1"""))
        assertEquals(result.stderr, "")
        assertEquals(result.stdout, "")
      }
    }
  }

  @Test
  fun codeInterpreterFail() {
    val sandbox = FlappySandbox()

    sandbox.use {
      runBlocking {
        assertFailsWith<FlappyJniException>("Unexpected at  => WASI error, source: WASI error") {
          it.evalPython(FlappySandbox.Input("""foo"""))
        }
      }
    }
  }
}
