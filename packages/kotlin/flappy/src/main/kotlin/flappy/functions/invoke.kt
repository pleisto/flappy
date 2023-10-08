package flappy.functions

import flappy.FlappyBaseAgent
import flappy.FlappyFunctionBase
import flappy.LLMGenerateConfig


class FlappyInvokeFunction<Args : Any, Ret : Any>(
  name: String,
  description: String,
  args: Class<Args>,
  returnType: Class<Ret>,
  val invoker: suspend (args: Args, agent: FlappyBaseAgent) -> Ret,
) : FlappyFunctionBase<Args, Ret>(
  name,
  description,
  args,
  returnType
) {
  override suspend fun invoke(args: Args, agent: FlappyBaseAgent, config: LLMGenerateConfig?): Ret =
    invoker(args, agent)

}
