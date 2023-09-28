package flappy

import com.aallam.openai.client.OpenAI
import flappy.annotations.FlappyField
import flappy.llms.ChatGPT
import kotlin.test.Test
import kotlin.test.assertEquals

class AgentTest {
  class SampleArguments(
    @FlappyField(description = "Lawsuit full text.")
    val lawsuit: String
  )

  enum class Verdict {
    Innocent, Guilty, Unknown
  }

  class SampleReturn(
    @FlappyField
    val verdict: Verdict,

    @FlappyField
    val plaintiff: String,

    @FlappyField
    val defendant: String,

    @FlappyField
    val judgeOptions: List<String>
  )

  private val sampleFunction = FlappySynthesizedFunction(
    name = "getMeta",
    description = "Extract meta data from a lawsuit full text.",
    args = SampleArguments::class.java,
    returnType = SampleReturn::class.java,
  )

  val llm = ChatGPT(model = "gpt-3.5-turbo", openai = OpenAI(token = "your-token-key"))

  val lawAgent = FlappyBaseAgent(
    inferenceLLM = llm,
    functions = listOf(sampleFunction)
  )

  @Test
  fun functionCheck() {

    assertEquals(
      lawAgent.functionDefinitionString,
      """[{"name":"getMeta","description":"Extract meta data from a lawsuit full text.","parameters":{"properties":{"args":{"properties":{"lawsuit":{"type":"string","description":"Lawsuit full text."}},"required":["lawsuit"],"description":"Function arguments","type":"object"},"returnType":{"properties":{"verdict":{"type":"string","enum":["Innocent","Guilty","Unknown"]},"plaintiff":{"type":"string"},"defendant":{"type":"string"},"judgeOptions":{"type":"array","items":{"type":"string"}}},"required":["verdict","plaintiff","defendant","judgeOptions"],"description":"Function return type","type":"object"}},"type":"object"}}]"""
    )

  }


  @Test
  fun xxx() {
    assertEquals(
      lawAgent.lanOutputSchemaString,
      """{"items":{"properties":{"id":{"type":"int","description":"Increment id starting from 1"},"functionName":{"type":"string"},"args":{"description":"an object encapsulating all arguments for a function call. If an argument's value is derived from the return of a previous step, it should be as '%@_' + the ID of the previous step (e.g. '%@_1'). If an 'returnType' in **previous** step's function's json schema is object, '.' should be used to access its properties, else just use id with prefix. This approach should remain compatible with the 'args' attribute in the function's JSON schema.","type":"object"},"thought":{"type":"string","description":"The thought why this step is needed."}},"required":["id","functionName","args","thought"],"description":"Base step.","type":"object"},"type":"array","description":"An array storing the steps."}"""
    )
  }

}
