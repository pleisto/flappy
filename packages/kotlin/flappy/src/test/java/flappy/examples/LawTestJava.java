package flappy.examples;

import flappy.FlappyBaseAgent;
import flappy.LLMResponse;
import flappy.llms.Dummy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.example.java.Law.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LawTestJava {
  @Test
  public void law() throws ExecutionException, InterruptedException {
    Dummy dummy = new Dummy(((_message, source, _cfg) -> {
      switch (source) {
        case FlappyBaseAgent.AGENT_SOURCE:
          return new LLMResponse.Success(
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
                    "lawsuit": "%@_1"
                  },
                  "thought": "Extract metadata from the lawsuit."
                }
              ]
                """);
      }

      if (source.equals(lawGetMeta.getSource())) {
        return new LLMResponse.Success(
          """
              {
              "verdict": "Unknown",
              "plaintiff": "families of victims of Sandy Hook Elementary School shooting",
              "defendant": "Alex Jones",
              "judgeOptions": ["reduce personal expenses to a reasonable level", "bar from further waste of estate assets", "appoint a trustee to oversee spending", "dismiss the bankruptcy case"]
            }
            """.trim());
      }
      return new LLMResponse.Success("");
    }));

    FlappyBaseAgent lawAgent = new FlappyBaseAgent(
      dummy, Arrays.asList(lawGetMeta, lawGetLatestLawsuitsByPlaintiff)
    );

    Future<LawMetaReturn> returnFuture = lawAgent.executePlanAsync(LAW_EXECUTE_PLAN_PROMPT);

    LawMetaReturn ret = returnFuture.get();

    assertEquals(Verdict.Unknown, ret.getVerdict());
  }

}
