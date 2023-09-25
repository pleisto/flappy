package flappy

import java.util.logging.Logger

interface FlappyLLM {
    interface GenerateConfig {
        val maxTokens: Int
        val temperature: Double
        val topP: Double
    }

    sealed interface Response {
        val success: Boolean
        val data: String
    }

    class SuccessLLMResponse(override val data: String) : Response {
        override val success = true

    }

    class FailLLMResponse(override val data: String) : Response {
        override val success = false
    }
}


abstract class FlappyLLMBase {
    open val defaultMaxTokens: Int = 0
    protected val logger: Logger = Logger.getLogger(this.javaClass.name)

    abstract suspend fun chatComplete(
        messages: List<FlappyChatMessage>,
        config: FlappyLLM.GenerateConfig? = null
    ): FlappyLLM.Response

}