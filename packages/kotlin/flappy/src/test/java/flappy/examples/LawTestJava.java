package flappy.examples;

import flappy.FlappyBaseAgent;
import flappy.FlappyLLM;
import flappy.llms.Dummy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static flappy.AgentKt.AGENT_SOURCE;
import static org.example.java.Law.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LawTestJava {
  @Test
  public void law() throws ExecutionException, InterruptedException {
    Dummy dummy = new Dummy(((_message, source, _cfg) -> {
      switch (source) {
        case AGENT_SOURCE:
          return new FlappyLLM.SuccessLLMResponse(
            """
              [
                {
                  "id": 1,
                  "functionName": "getLatestLawsuitsByPlaintiff",
                  "args": {
                    "plaintiff": "families of victims",
                    "arg1": false
                  },
                  "thought": "Get the latest lawsuits where the plaintiff is families of victims."
                },
                {
                  "id": 2,
                  "functionName": "getMeta",
                  "args": {
                    "lawsuit": "%@_1.output"
                  },
                  "thought": "Extract metadata from the lawsuit."
                }
              ]
                """);
      }

      if (source.equals(lawGetMeta.getSource())) {
        return new FlappyLLM.SuccessLLMResponse(
          """
              {
              "verdict": "Unknown",
              "plaintiff": "families of victims of Sandy Hook Elementary School shooting",
              "defendant": "Alex Jones",
              "judgeOptions": ["reduce personal expenses to a reasonable level", "bar from further waste of estate assets", "appoint a trustee to oversee spending", "dismiss the bankruptcy case"]
            }
            """.trim());
      }
      return new FlappyLLM.SuccessLLMResponse("");
    }));

    FlappyBaseAgent lawAgent = new FlappyBaseAgent(
      dummy, List.of(lawGetMeta, lawGetLatestLawsuitsByPlaintiff)
    );

    Future<LawMetaReturn> returnFuture = lawAgent.executePlanAsync(LAW_EXECUTE_PLAN_PROMPT);

    LawMetaReturn ret = returnFuture.get();

    assertEquals(Verdict.Unknown, ret.getVerdict());
  }

}
