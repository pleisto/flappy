package flappy

import java.util.logging.Logger

internal typealias AnyFlappyFeature = FlappyFeatureBase<*, *>

internal typealias AnyClass = Class<*>

abstract class FlappyFeatureBase<Args : Any, Ret : Any>(
  val name: String,
  val argsType: Class<Args>,
  val returnType: Class<Ret>,
) : AutoCloseable {
  protected val logger: Logger = Logger.getLogger(this.javaClass.name)

  val source = "#feature#$name"

  private val argsTypeSchemaProperties = this.argsType.buildFieldProperties("Function arguments")
  private val returnTypeSchemaProperties = this.returnType.buildFieldProperties("Function return type")

  val argsTypeSchemaPropertiesString: String = argsTypeSchemaProperties.asJSON()
  val returnTypeSchemaPropertiesString: String = returnTypeSchemaProperties.asJSON()

  internal abstract fun buildDescription(): String

  internal fun definition() = FunctionSchema(
    name = name,
    description = buildDescription(),
    parameters = FunctionParameters(
      FunctionProperties(
        args = argsTypeSchemaProperties,
        returnType = returnTypeSchemaProperties
      )
    )
  )


  private fun buildSystemMessage() = SystemMessage(
    """
            ${buildDescription()}
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

  protected fun parseComplete(resp: LLMResponse): Ret {
    if (!resp.success) throw FlappyException.NonRepairableException(resp.data)
    if (isInvalidJson(resp.data)) throw FlappyException.RepairableException("invalid json")

    return resp.data.castBy(returnType)
  }

  abstract suspend fun invoke(args: Args, agent: FlappyBaseAgent, config: LLMGenerateConfig? = null): Ret

  suspend fun call(args: String, agent: FlappyBaseAgent, config: LLMGenerateConfig? = null) =
    invoke(args.castBy(argsType), agent, config)

  suspend fun call(args: Args, agent: FlappyBaseAgent, config: LLMGenerateConfig? = null) {
    invoke(if (args is String) (args.castBy(argsType)) else args, agent, config)
  }

}
