package flappy.functions

import flappy.FlappyBaseAgent
import flappy.FlappyFunctionBase
import flappy.LLMGenerateConfig


class FlappyInvokeFunction<Args : Any, Ret : Any>(
  name: String,
  val description: String,
  args: Class<Args>,
  returnType: Class<Ret>,
  val invoker: suspend (args: Args, agent: FlappyBaseAgent) -> Ret,
) : FlappyFunctionBase<Args, Ret>(
  name = name,
  argsType = args,
  returnType = returnType
) {
  override fun buildDescription(): String = description

  override suspend fun invoke(args: Args, agent: FlappyBaseAgent, config: LLMGenerateConfig?): Ret =
    invoker(args, agent)

  override fun close() {}

}
