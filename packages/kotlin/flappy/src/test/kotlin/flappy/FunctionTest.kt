package flappy

import flappy.annotations.FlappyField
import flappy.annotations.flappyFieldMetadataList
import flappy.features.FlappyInvokeFunction
import flappy.features.FlappySynthesizedFunction
import flappy.llms.Dummy
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FunctionTest {
  class SampleArguments(
    @FlappyField(description = "Lawsuit full text.")
    val argsString: String,

    @FlappyField(description = "Long foo bar")
    val argsLong: Long
  )

  enum class EnumType {
    Innocent, Guilty, Unknown
  }


  class SampleReturn(
    @FlappyField
    val enum: EnumType,

    @FlappyField
    val optionalString: String?,

    @FlappyField
    val string: String,

    @FlappyField
    val int: Int,

    @FlappyField
    val long: Long,

    @FlappyField
    val bool: Boolean,

    @FlappyField
    val double: Double,

    @FlappyField
    val float: Float,

    @FlappyField
    val any: Any,

    @FlappyField
    val map: Map<String, Any>,

    @FlappyField
    val listString: List<String>,

    @FlappyField
    val arrayString: Array<String>,

    @FlappyField
    val arrayInteger: Array<Int>,

    @FlappyField
    val arrayEnum: Array<EnumType>,

    )

  private val sampleFunction = FlappySynthesizedFunction(
    name = "getMeta",
    description = "Extract meta data from a lawsuit full text.",
    args = SampleArguments::class.java,
    returnType = SampleReturn::class.java,
  )


  @Test
  fun argsSchemaPropertiesString() {
    assertEquals(
      sampleFunction.argsTypeSchemaPropertiesString,
      """{"properties":{"argsString":{"type":"string","description":"Lawsuit full text."},"argsLong":{"type":"long","description":"Long foo bar"}},"required":["argsString","argsLong"],"description":"Function arguments","type":"object"}"""
    )

    assertEquals(
      sampleFunction.returnTypeSchemaPropertiesString,
      """{"properties":{"enum":{"type":"string","enum":["Innocent","Guilty","Unknown"]},"optionalString":{"type":"string"},"string":{"type":"string"},"int":{"type":"int"},"long":{"type":"long"},"bool":{"type":"boolean"},"double":{"type":"double"},"float":{"type":"float"},"any":{"type":"object"},"map":{"type":"object"},"listString":{"type":"array","items":{"type":"string"}},"arrayString":{"type":"array","items":{"type":"string"}},"arrayInteger":{"type":"array","items":{"type":"int"}},"arrayEnum":{"type":"array","items":{"type":"string","enum":["Innocent","Guilty","Unknown"]}}},"required":["enum","optionalString","string","int","long","bool","double","float","any","map","listString","arrayString","arrayInteger","arrayEnum"],"description":"Function return type","type":"object"}"""
    )

    assertEquals(
      sampleFunction.definition().asJSON(),
      """{"name":"getMeta","description":"Extract meta data from a lawsuit full text.","parameters":{"properties":{"args":{"properties":{"argsString":{"type":"string","description":"Lawsuit full text."},"argsLong":{"type":"long","description":"Long foo bar"}},"required":["argsString","argsLong"],"description":"Function arguments","type":"object"},"returnType":{"properties":{"enum":{"type":"string","enum":["Innocent","Guilty","Unknown"]},"optionalString":{"type":"string"},"string":{"type":"string"},"int":{"type":"int"},"long":{"type":"long"},"bool":{"type":"boolean"},"double":{"type":"double"},"float":{"type":"float"},"any":{"type":"object"},"map":{"type":"object"},"listString":{"type":"array","items":{"type":"string"}},"arrayString":{"type":"array","items":{"type":"string"}},"arrayInteger":{"type":"array","items":{"type":"int"}},"arrayEnum":{"type":"array","items":{"type":"string","enum":["Innocent","Guilty","Unknown"]}}},"required":["enum","optionalString","string","int","long","bool","double","float","any","map","listString","arrayString","arrayInteger","arrayEnum"],"description":"Function return type","type":"object"}},"type":"object"}}"""
    )
  }


  @Test
  fun functionInit() {
    class SampleArguments

    assertEquals(SampleArguments::class.java.flappyFieldMetadataList().size, 0)
  }


  @Test
  fun parse() {
    assertFailsWith<FlappyException.ParseException> {
      "".castBy(sampleFunction.returnType)
    }

    assertFailsWith<FlappyException.ParseException> {
      "".castBy(sampleFunction.argsType)
    }

    assertEquals("{\"argsString\":\"foo\"}".castBy(sampleFunction.argsType).argsString, "foo")
    assertEquals("{\"argsString\":\"foo\"}".castBy(sampleFunction.argsType).argsLong, 0)

    assertEquals("{\"argsString\":\"foo\",\"argsLong\":1}".castBy(sampleFunction.argsType).argsLong, 1)
  }

  @Test
  fun dump() {
    val args = SampleArguments("foo", 1)
    assertEquals(sampleFunction.dumpArgs(args), "{\"argsString\":\"foo\",\"argsLong\":1}")
  }

  @Test
  fun law() {
    class GetLatestLawsuitsArguments(
      @FlappyField
      val plaintiff: String,

      @FlappyField(description = "For demo purpose. set to False")
      val arg1: Boolean,

      @FlappyField(description = "ignore it", optional = true)
      val arg2: List<String>?
    )


    val lawGetLatestLawsuitsByPlaintiff = FlappyInvokeFunction(
      name = "getLatestLawsuitsByPlaintiff",
      description = "Get the latest lawsuits by plaintiff.",
      args = GetLatestLawsuitsArguments::class.java,
      returnType = String::class.java,
      invoker = { args, _ -> args.plaintiff }
    )

    val lawAgent = FlappyBaseAgent(
      maxRetry = 3,
      inferenceLLM = Dummy(),
      features = listOf(lawGetLatestLawsuitsByPlaintiff)
    )

    runBlocking {
      val result = lawGetLatestLawsuitsByPlaintiff.call("""{"plaintiff":"foo", "arg1":true}""", lawAgent)
      assertEquals(result, "foo")
    }

  }

}
