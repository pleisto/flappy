package org.example.kotlin

import flappy.FlappyBaseAgent
import flappy.features.FlappyCodeInterpreter
import flappy.llms.ChatGPT
import io.github.cdimascio.dotenv.dotenv

suspend fun main(args: Array<String>) {
  val dotenv = dotenv()

  val chatGPT = ChatGPT(
    ChatGPT.ChatGPTConfig(token = dotenv["OPENAI_TOKEN"], host = dotenv["OPENAI_API_BASE"])
  )

  val agent = FlappyBaseAgent(
    maxRetry = 2,
    inferenceLLM = chatGPT,
    features = listOf(FlappyCodeInterpreter())
  )

  agent.use {
    it.executePlan<String>("There are some rabbits and chickens in a barn. What is the number of chickens if there are 396 legs and 150 heads in the barn?")

//    it.callFeature<String>(
//      FlappyCodeInterpreter.DEFAULT_NAME,
//      "There are some rabbits and chickens in a barn. What is the number of chickens if there are 396 legs and 150 heads in the barn?"
//    )
  }

}
