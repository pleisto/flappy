package flappy

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.readValue
import flappy.annotations.FlappyField
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger

class FlappyBaseAgent @JvmOverloads constructor(
  val inferenceLLM: FlappyLLMBase,
  private val functions: List<AnyFlappyFunction>,
  maxRetry: Int? = null,
) : AutoCloseable {

  override fun close() {
    inferenceLLM.close()

    functions.map { it.close() }
  }

  companion object {
    /** @suppress */
    const val AGENT_SOURCE = "#agent"
  }

  private val logger = Logger.getLogger(this.javaClass.name)
  internal val finalMaxRetry = maxRetry ?: 1

  private val functionsDefinition = functions.map { it.definition() }

  private data class BaseStep(
    @FlappyField(description = "Increment id starting from 1")
    val id: Int,

    @FlappyField
    val functionName: String,

    @FlappyField(
      description = "an object encapsulating all arguments for a function call. If an argument's value is derived from the return of a previous step, it should be as '$STEP_PREFIX' + the ID of the previous step (e.g. '${STEP_PREFIX}1'). If an argument's value is derived from the **previous** step's function's return value's properties, '.' should be used to access its properties, else just use id with prefix. This approach should remain compatible with the 'args' attribute in the function's JSON schema."
    )
    val args: Map<String, Any>,

    @FlappyField(description = "The thought why this step is needed.")
    val thought: String
  )

  private val stepSchema = BaseStep::class.java.buildFieldProperties("Base step.")

  internal val functionDefinitionString: String = functionsDefinition.asYAML()
  internal val lanOutputSchemaString: String = object {
    val items = stepSchema
    val type = FieldType.LIST.typeName
    val description = "An array storing the steps."
  }.asJSON(prettyPrint = true)

  init {
    if (finalMaxRetry <= 0) throw FlappyException.CompileException("retry should be positive")
    if (functions.isEmpty()) throw FlappyException.CompileException("functions is blank")

    val names = functions.map { it.name }
    if (names.size != names.toSet().size) throw FlappyException.CompileException("functions is not unique")
  }


  private fun findFunction(name: String) =
    functions.find { it.name == name }
      ?: throw FlappyException.FatalException("function `$name` not found, supported: ${functions.map { it.name }}")

  private fun buildUserMessage(prompt: String) = UserMessage(
    """
            Prompt: $prompt

            Plan array:
        """.trimIndent()
  )


  private fun buildSystemMessage() = SystemMessage(
    """
        You are an AI assistant that makes step-by-step plans to solve problems, utilizing external functions. Each step entails one plan followed by a function-call, which will later be executed to gather args for that step.
        Make as few plans as possible if it can solve the problem.
        The functions list is described using the following YAML schema array:
        $functionDefinitionString

        Your specified plans should be output as JSON object array and adhere to the following JSON schema:
        $lanOutputSchemaString

        Only the listed functions are allowed to be used.
    """.trimIndent()
  )

  private fun buildExecuteMessage(prompt: String) = listOf(buildSystemMessage(), buildUserMessage(prompt))


  suspend fun <R> executePlan(prompt: String): R {
    val chatMessages = this.buildExecuteMessage(prompt)

    logger.info(chatMessages.toString())

    val resp = inferenceLLM.chatComplete(chatMessages, AGENT_SOURCE)
    if (!resp.success) throw RuntimeException(resp.data)
    val steps = parseSteps(resp.data)

    val resultStore: ResultStore = mutableMapOf()
    if (steps.isEmpty()) throw RuntimeException("steps is empty")

    for ((id, functionName, args, _) in steps) {
      val newArgs = args.mapValues { fetchStepArgs(it.value, resultStore) }
      val newArgsString = jacksonMapper.writeValueAsString(newArgs)

      logger.info("before call $id $functionName $newArgsString")

      val result = callFunction<Any>(functionName, newArgsString)

      logger.info("after call $id $functionName ${jacksonMapper.writeValueAsString(result)}")

      resultStore[id] = result
    }

    val result = resultStore[steps.last().id]!!
    logger.info("result: $result")
    @Suppress("UNCHECKED_CAST")
    return result as R
  }

  // https://stackoverflow.com/a/52887677/20030734
  @OptIn(DelicateCoroutinesApi::class)
  fun <R> executePlanAsync(prompt: String): CompletableFuture<R> = GlobalScope.future { executePlan(prompt) }

  private fun fetchStepArgs(value: Any, resultStore: ResultStore): Any {
    if (value is String && value.startsWith(STEP_PREFIX)) {
      val keys = value.removePrefix(STEP_PREFIX).split(".")
      if (keys.isEmpty()) return value
      val stepId = keys.first().toInt()
      val result = resultStore[stepId] ?: throw RuntimeException("StepId $stepId not found")
      if (keys.size == 1) return result

      return keys.slice(1..<keys.size).fold(result) { cur, acc ->
        return cur.getField<Any>(acc)
      }

    }
    return value
  }

  private fun castSteps(input: String): List<BaseStep> {
    try {
      return jacksonMapper.readValue<List<BaseStep>>(input)
    } catch (e: MismatchedInputException) {
      throw FlappyException.ParseException(e.message ?: e.originalMessage)
    }
  }

  private fun parseSteps(data: String): List<BaseStep> {
    val startIndex = data.indexOf("[")
    val endIndex = data.lastIndexOf("]")
    if (startIndex !in 0..<endIndex) throw FlappyException.ParseException("Invalid JSON response")
    val body = data.slice(startIndex..endIndex)
    return castSteps(body)
  }

  private suspend fun <R> callFunction(name: String, args: String): R {
    val f = this.findFunction(name)
    @Suppress("UNCHECKED_CAST")
    return f.call(args, this) as R
  }

  @OptIn(DelicateCoroutinesApi::class)
  fun <R> callFunctionAsync(name: String, args: String): CompletableFuture<R> =
    GlobalScope.future { callFunction(name, args) }
}


internal typealias ResultStore = MutableMap<Int, Any>
