package flappy.llms

import flappy.FlappyChatMessage
import flappy.FlappyLLMBase
import flappy.LLMGenerateConfig
import flappy.LLMResponse

internal typealias MockChat = (
  messages: List<FlappyChatMessage>,
  source: String,
  config: LLMGenerateConfig?
) -> LLMResponse

internal val defaultMockChat: MockChat = { _, _, _ -> LLMResponse.Success("") }

/** @suppress */
class Dummy(
  val mockChat: MockChat = defaultMockChat
) : FlappyLLMBase() {
  override fun close() {}
  
  override suspend fun chatComplete(
    messages: List<FlappyChatMessage>,
    source: String,
    config: LLMGenerateConfig?
  ): LLMResponse {
    return mockChat(messages, source, config)
  }
}
