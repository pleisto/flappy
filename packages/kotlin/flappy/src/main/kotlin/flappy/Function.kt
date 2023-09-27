package flappy

import java.util.logging.Logger

typealias AnyFlappyFunction = FlappyFunction<*, *>

typealias AnyClass = Class<*>

abstract class FlappyFunction<Args : Any, Ret : Any>(
  val name: String,
  private val description: String,
  val argsType: Class<Args>,
  val returnType: Class<Ret>,
) {
  protected val logger: Logger = Logger.getLogger(this.javaClass.name)

  val source = "#function#$name"

  private val argsTypeSchemaProperties = this.argsType.buildFieldProperties("Function arguments")
  private val returnTypeSchemaProperties = this.returnType.buildFieldProperties("Function return type")

  val argsTypeSchemaPropertiesString: String = argsTypeSchemaProperties.asString()
  val returnTypeSchemaPropertiesString: String = returnTypeSchemaProperties.asString()

  fun definition() = FunctionSchema(
    name = name,
    description = description,
    parameters = FunctionParameters(
      FunctionProperties(
        args = argsTypeSchemaProperties,
        returnType = returnTypeSchemaProperties
      )
    )
  )


  private fun buildSystemMessage() = SystemMessage(
    """
            $description
            User request according to the following JSON Schema:
            $argsTypeSchemaPropertiesString

            Translate it into JSON objects according to the following JSON Schema: $returnTypeSchemaPropertiesString
        """.trimIndent()
  )

  private fun buildUserMessage(args: Args) = UserMessage(
    """
        User request:${dumpArgs(args)}

        json object:
    """.trimIndent()
  )

  protected fun buildChatMessages(args: Args) = listOf(buildSystemMessage(), buildUserMessage(args))

  protected fun buildRepairMessages(content: String, reason: String) = listOf(
    AssistantMessage(content),
    UserMessage(
      """
            You response is invalid for the following reason:
            $reason
            Please try again.
        """.trimIndent()
    )
  )

  fun dumpArgs(args: Args): String {
    return jacksonMapper.writeValueAsString(args)
  }

  protected fun parseComplete(resp: FlappyLLM.Response): Ret {
    if (!resp.success) throw NonRepairableException(resp.data)
    if (isInvalidJson(resp.data)) throw RepairableException("invalid json")

    return resp.data.castBy(returnType)
  }

  abstract suspend fun invoke(args: Args, agent: FlappyBaseAgent, config: FlappyLLM.GenerateConfig? = null): Ret

  suspend fun call(args: String, agent: FlappyBaseAgent, config: FlappyLLM.GenerateConfig? = null) =
    invoke(args.castBy(argsType), agent, config)

  suspend fun call(args: Args, agent: FlappyBaseAgent, config: FlappyLLM.GenerateConfig? = null) {
    invoke(if (args is String) (args.castBy(argsType)) else args, agent, config)
  }
}

class FlappySynthesizedFunction<Args : Any, Ret : Any>(
  name: String,
  description: String,
  args: Class<Args>,
  returnType: Class<Ret>
) : FlappyFunction<Args, Ret>(
  name,
  description,
  args,
  returnType
) {
  override suspend fun invoke(args: Args, agent: FlappyBaseAgent, config: FlappyLLM.GenerateConfig?): Ret {
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
          is NonRepairableException -> {}
          else -> {
            repairMessages = buildRepairMessages(resp.data, e.message ?: e.localizedMessage)
          }
        }
      }
    }
  }
}

class FlappyInvokeFunction<Args : Any, Ret : Any>(
  name: String,
  description: String,
  args: Class<Args>,
  returnType: Class<Ret>,
  val invoker: suspend (args: Args, agent: FlappyBaseAgent) -> Ret,
) : FlappyFunction<Args, Ret>(
  name,
  description,
  args,
  returnType
) {
  override suspend fun invoke(args: Args, agent: FlappyBaseAgent, config: FlappyLLM.GenerateConfig?): Ret =
    invoker(args, agent)

}
