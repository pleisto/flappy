package flappy

import com.pleisto.FlappyJniException
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class JniTest {

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
