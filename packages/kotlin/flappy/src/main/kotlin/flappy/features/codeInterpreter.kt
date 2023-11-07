package flappy.features

import flappy.*

internal typealias ArgsType = String
internal typealias RetType = String


class FlappyCodeInterpreter(private val network: Boolean = false) : FlappyFeatureBase<ArgsType, RetType>(
  name = "sandbox",
  argsType = String::class.java,
  returnType = String::class.java
) {
  override fun buildDescription() =
    Template.render(
      "features/codeInterpreter/description.mustache",
      mapOf("enabled" to network)
    )


  private val sandbox = FlappySandbox()

  override suspend fun invoke(args: ArgsType, agent: FlappyBaseAgent, config: LLMGenerateConfig?): RetType {
    this.logger.info("eval $args")
    if (!args.startsWith("def main():")) throw FlappyException.EvalException("Function \"main\" not found")

    val code = Template.render(
      "features/codeInterpreter/evalCode.mustache",
      mapOf("code" to args)
    )
    val result = sandbox.evalPython(FlappySandbox.Input(code = code, network = network))

    if (result.stderr.isNotEmpty()) {
      throw FlappyException.EvalException(result.stderr)
    }

    return result.stdout
  }

  override fun close() {
    sandbox.close()
  }
}
