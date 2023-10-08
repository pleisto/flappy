package flappy

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger

interface LLMGenerateConfig {
  val maxTokens: Int
  val temperature: Double
  val topP: Double
}

sealed class LLMResponse(open val data: String) {
  abstract val success: Boolean

  override fun toString() = "[$success] $data"

  data class Success(override val data: String) : LLMResponse(data) {
    override val success = true
  }

  data class Fail(override val data: String) : LLMResponse(data) {
    override val success = false
  }
}


abstract class FlappyLLMBase {
  open val defaultMaxTokens: Int = 0
  protected val logger: Logger = Logger.getLogger(this.javaClass.name)

  abstract suspend fun chatComplete(
    messages: List<FlappyChatMessage>,
    source: String,
    config: LLMGenerateConfig? = null
  ): LLMResponse

  @OptIn(DelicateCoroutinesApi::class)
  fun chatCompleteAsync(
    messages: List<FlappyChatMessage>,
    source: String,
    config: LLMGenerateConfig? = null
  ): CompletableFuture<LLMResponse> = GlobalScope.future { chatComplete(messages, source, config) }
}
