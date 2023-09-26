package flappy.examples

import flappy.AGENT_SOURCE
import flappy.FlappyBaseAgent
import flappy.FlappyLLM
import flappy.llms.Dummy
import kotlinx.coroutines.runBlocking
import org.example.kotlin.*
import kotlin.test.Test
import kotlin.test.assertEquals

val lawDummy = Dummy { _, source, _ ->
  when (source) {
    AGENT_SOURCE -> FlappyLLM.SuccessLLMResponse(
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
        """.trimIndent()
    )

    lawGetMeta.source -> FlappyLLM.SuccessLLMResponse(
      """
          {
            "verdict": "Unknown",
            "plaintiff": "families of victims of Sandy Hook Elementary School shooting",
            "defendant": "Alex Jones",
            "judgeOptions": ["reduce personal expenses to a reasonable level", "bar from further waste of estate assets", "appoint a trustee to oversee spending", "dismiss the bankruptcy case"]
          }
        """.trimIndent()
    )

    else -> FlappyLLM.SuccessLLMResponse("")
  }
}

class LawTest {
  @Test
  fun law() {

    val lawAgent = FlappyBaseAgent(
      maxRetry = 2,
      inferenceLLM = lawDummy,
      functions = listOf(lawGetMeta, lawGetLatestLawsuitsByPlaintiff)
    )

    runBlocking {
      val ret =
        lawAgent.executePlan<LawMetaReturn>(LAW_EXECUTE_PLAN_PROMPT)

      assertEquals(ret.verdict, Verdict.Unknown)
    }
  }
}
