package flappy.llms

import flappy.FlappyChatMessage
import flappy.FlappyLLM
import flappy.FlappyLLMBase

typealias MockChat = (
  messages: List<FlappyChatMessage>,
  source: String,
  config: FlappyLLM.GenerateConfig?
) -> FlappyLLM.Response

val defaultMockChat: MockChat = { _, _, _ -> FlappyLLM.SuccessLLMResponse("") }

class Dummy(
  val mockChat: MockChat = defaultMockChat
) : FlappyLLMBase() {
  override suspend fun chatComplete(
    messages: List<FlappyChatMessage>,
    source: String,
    config: FlappyLLM.GenerateConfig?
  ): FlappyLLM.Response {
    return mockChat(messages, source, config)
  }
}
