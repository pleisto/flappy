package flappy

import com.pleisto.FlappyJniSandbox
import kotlinx.coroutines.future.await

class FlappySandbox : AutoCloseable {
  data class Input(
    val code: String,
    val network: Boolean = false,
    val envs: Map<String, String> = mapOf()
  )

  data class Result(
    val stdout: String,
    val stderr: String
  )

  private val instance = FlappyJniSandbox()

  suspend fun evalPython(input: Input): Result {
    val resp = instance.evalPythonCode(
      FlappyJniSandbox.FlappyJniSandboxInput(
        input.code, input.network, input.envs
      )
    ).await()

    return Result(stdout = resp.stdout, stderr = resp.stderr)
  }

  override fun close() {}
}
