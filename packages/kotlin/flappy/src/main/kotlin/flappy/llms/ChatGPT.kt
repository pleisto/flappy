package flappy.llms

import com.theokanning.openai.client.OpenAiApi
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.completion.chat.ChatMessageRole
import com.theokanning.openai.service.OpenAiService
import com.theokanning.openai.service.OpenAiService.*
import flappy.*
import okhttp3.OkHttpClient
import java.time.Duration


class ChatGPT(
  private val chatGPTConfig: ChatGPTConfig,
) :
  FlappyLLMBase() {
  override val defaultMaxTokens = -1

  //  https://platform.openai.com/docs/models/overview
  enum class OpenaiModel(val value: String) {
    GPT3("gpt-3.5-turbo-1106"),
    GPT4("gpt-4"),
    GPT4_32K("gpt-4-32k"),
    GPT3_16K("gpt-3.5-turbo-16k")
  }

  private val connectDuration = Duration.ZERO

  private val client: OkHttpClient = defaultClient(chatGPTConfig.token, connectDuration)

  private fun buildOpenAiService(config: ChatGPTConfig): OpenAiService {
    val mapper = defaultObjectMapper()
    val retrofit = if (config.host == null) {
      defaultRetrofit(client, mapper)
    } else {
      defaultRetrofit(client, mapper).newBuilder().baseUrl(config.host).build()
    }

    val api: OpenAiApi = retrofit.create(OpenAiApi::class.java)
    return OpenAiService(api)
  }

  private val service: OpenAiService = buildOpenAiService(chatGPTConfig)

  override fun close() {
    client.dispatcher.executorService.shutdown()
  }

  class ChatGPTConfig @JvmOverloads constructor(
    model: OpenaiModel? = null,
    val token: String,
    val host: String? = null
  ) {
    val chatGPTModel = model ?: OpenaiModel.GPT3
  }

  override suspend fun chatComplete(
    messages: List<FlappyChatMessage>,
    source: String,
    config: LLMGenerateConfig?
  ): LLMResponse {
    logger.info("openai input $messages")

    val chatMessages = messages.map {
      ChatMessage(
        (when (it.role) {
          FlappyRole.ASSISTANT -> ChatMessageRole.ASSISTANT.value()
          FlappyRole.SYSTEM -> ChatMessageRole.SYSTEM.value()
          FlappyRole.USER -> ChatMessageRole.USER.value()
        }),
        it.content
      )
    }

    val chatCompletionRequest =
      ChatCompletionRequest.builder().model(chatGPTConfig.chatGPTModel.value).messages(chatMessages).n(1)
        .topP(config?.topP)
        .temperature(config?.temperature).build()

    val result = service.createChatCompletion(chatCompletionRequest)

    logger.info("openai output $result")

    val choice = result.choices.firstOrNull()

    return if (choice === null)
      LLMResponse.Fail("failed") else
      LLMResponse.Success(choice.message.content)
  }
}
