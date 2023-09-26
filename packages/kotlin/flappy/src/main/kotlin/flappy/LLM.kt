package flappy

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger

interface FlappyLLM {
  interface GenerateConfig {
    val maxTokens: Int
    val temperature: Double
    val topP: Double
  }

  abstract class Response(open val data: String) {
    abstract val success: Boolean

    override fun toString() = "[$success] $data"
  }

  class SuccessLLMResponse(override val data: String) : Response(data) {
    override val success = true

  }

  class FailLLMResponse(override val data: String) : Response(data) {
    override val success = false
  }
}


abstract class FlappyLLMBase {
  open val defaultMaxTokens: Int = 0
  protected val logger: Logger = Logger.getLogger(this.javaClass.name)

  abstract suspend fun chatComplete(
    messages: List<FlappyChatMessage>,
    source: String,
    config: FlappyLLM.GenerateConfig? = null
  ): FlappyLLM.Response

  @OptIn(DelicateCoroutinesApi::class)
  fun chatCompleteAsync(
    messages: List<FlappyChatMessage>,
    source: String,
    config: FlappyLLM.GenerateConfig? = null
  ): CompletableFuture<FlappyLLM.Response> = GlobalScope.future { chatComplete(messages, source, config) }
}
