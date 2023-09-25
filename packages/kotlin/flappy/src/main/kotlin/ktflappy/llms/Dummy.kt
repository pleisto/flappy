package flappy.llms

import flappy.FlappyChatMessage
import flappy.FlappyLLM
import flappy.FlappyLLMBase

class Dummy : FlappyLLMBase() {
    override suspend fun chatComplete(
        messages: List<FlappyChatMessage>,
        config: FlappyLLM.GenerateConfig?
    ): FlappyLLM.Response {
        return FlappyLLM.SuccessLLMResponse("")
    }
}