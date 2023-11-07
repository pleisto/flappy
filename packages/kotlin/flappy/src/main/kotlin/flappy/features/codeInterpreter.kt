package flappy.features

import flappy.*

internal typealias ArgsType = String
internal typealias RetType = String


class FlappyCodeInterpreter(private val network: Boolean = false) : FlappyFeatureBase<ArgsType, RetType>(
  name = "sandbox",
  argsType = String::class.java,
  returnType = String::class.java
) {
  override fun buildDescription(): String {
    return """
      An safe sandbox that only support the built-in library. The execution time is limited to 120 seconds. The task is to define a function named "main" that doesn't take any parameters. The output should be a String.
              Network access is ${
      if (network) {
        "enabled"
      } else {
        "disabled"
      }
    }
    """.trimIndent()
  }

  private val sandbox = FlappySandbox()

  override suspend fun invoke(args: ArgsType, agent: FlappyBaseAgent, config: LLMGenerateConfig?): RetType {
    val result = sandbox.evalPython(FlappySandbox.Input(code = args, network = network))

    if (result.stderr.isNotEmpty()) {
      throw FlappyException.EvalException(result.stderr)
    }

    return result.stdout
  }

  override fun close() {
    sandbox.close()
  }
}
