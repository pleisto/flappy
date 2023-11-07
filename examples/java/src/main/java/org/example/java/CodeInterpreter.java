package org.example.java;


import flappy.FlappyBaseAgent;
import flappy.features.FlappyCodeInterpreter;
import flappy.llms.ChatGPT;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class CodeInterpreter {
  
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    Dotenv dotenv = Dotenv.load();
    ChatGPT llm = new ChatGPT(new ChatGPT.ChatGPTConfig(null, dotenv.get("OPENAI_TOKEN"), dotenv.get("OPENAI_API_BASE")));

    FlappyBaseAgent agent = new FlappyBaseAgent(
      llm, Collections.singletonList(new FlappyCodeInterpreter())
    );

    Future<String> future = agent.executePlanAsync("There are some rabbits and chickens in a barn. What is the number of chickens if there are 396 legs and 150 heads in the barn?");
    String ret = future.get();
  }
}
