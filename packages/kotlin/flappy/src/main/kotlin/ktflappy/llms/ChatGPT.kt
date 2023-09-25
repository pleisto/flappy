package flappy.llms

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import flappy.*


class ChatGPTConfig @JvmOverloads constructor(val token: String, val host: String? = null)

class ChatGPT @JvmOverloads constructor(
    private val model: String,
    private val chatGPTConfig: ChatGPTConfig? = null,
    private val openai: OpenAI? = null,
) :
    FlappyLLMBase() {
    private val openaiClient =
        openai ?: chatGPTConfig?.let {
            OpenAI(
                token = it.token,
                logging = LoggingConfig(logLevel = LogLevel.All),
                host = OpenAIHost(it.host ?: OpenAIHost.OpenAI.baseUrl)
            )
        }
        ?: throw CompileException("openai client missing")

    override val defaultMaxTokens: Int
        get() {
            // https://platform.openai.com/docs/models/overview
            return when (model) {
                "16k" -> 16385
                "32k" -> 32768
                "gpt-4" -> 8192
                else -> 4096
            }
        }


    override suspend fun chatComplete(
        messages: List<FlappyChatMessage>,
        config: FlappyLLM.GenerateConfig?
    ): FlappyLLM.Response {
        logger.info("openai input $messages")

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model),
            messages = messages.map {
                ChatMessage(
                    role = (when (it.role) {
                        FlappyRole.ASSISTANT -> ChatRole.Assistant
                        FlappyRole.SYSTEM -> ChatRole.System
                        FlappyRole.USER -> ChatRole.User
                    }),
                    content = it.content
                )
            },
            // maxTokens = config?.maxTokens ?: defaultMaxTokens,
            temperature = config?.temperature,
            topP = config?.topP
        )

        val completion: ChatCompletion = openaiClient.chatCompletion(chatCompletionRequest)

        logger.info("openai output $completion")
        val choice = completion.choices.firstOrNull()

        return if (choice === null)
            FlappyLLM.FailLLMResponse("failed") else
            FlappyLLM.SuccessLLMResponse(choice.message.content!!)
    }
}