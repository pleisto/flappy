package flappy.functions

import flappy.*

class FlappySynthesizedFunction<Args : Any, Ret : Any>(
  name: String,
  val description: String,
  args: Class<Args>,
  returnType: Class<Ret>
) : FlappyFunctionBase<Args, Ret>(
  name = name,
  argsType = args,
  returnType = returnType
) {
  override fun buildDescription(): String = description

  override suspend fun invoke(args: Args, agent: FlappyBaseAgent, config: LLMGenerateConfig?): Ret {
    val chatMessages = this.buildChatMessages(args)

    val maxRetry = agent.finalMaxRetry
    var retry = maxRetry
    var repairMessages: List<FlappyChatMessage> = listOf()

    while (true) {
      val finalMessage = chatMessages + repairMessages
      logger.info("$name call input $finalMessage")
      val resp = agent.inferenceLLM.chatComplete(finalMessage, source, config)
      logger.info("<$retry/$maxRetry> $name call resp $resp")
      try {
        return parseComplete(resp)
      } catch (e: Exception) {
        logger.info("<$retry/$maxRetry> $name $e")
        retry -= 1

        if (retry <= 0) throw RuntimeException("Interrupted, function call failed. Please refer to the error message above.")

        when (e) {
          is FlappyException.NonRepairableException -> {}
          else -> {
            repairMessages = buildRepairMessages(resp.data, e.message ?: e.localizedMessage)
          }
        }
      }
    }
  }

  override fun close() {}
}
