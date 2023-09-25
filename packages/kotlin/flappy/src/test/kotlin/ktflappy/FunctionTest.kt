package flappy

import kotlinx.coroutines.runBlocking
import flappy.annotations.FlappyField
import flappy.llms.Dummy
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

        @FlappyField(subType = FieldType.STRING)
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
    fun functionCheck() {
        assertEquals(sampleFunction.argsSchema.size(), 2)
    }

    @Test
    fun argsSchemaPropertiesString() {
        assertEquals(
            sampleFunction.argsSchemaPropertiesString,
            """{"properties":{"argsString":{"type":"string","description":"Lawsuit full text."},"argsLong":{"type":"long","description":"Long foo bar"}},"required":["argsString","argsLong"],"description":"Function arguments","type":"object"}"""
        )
    }

    @Test
    fun returnTypeSchemaPropertiesString() {
        assertEquals(
            sampleFunction.returnTypeSchemaPropertiesString,
            """{"properties":{"enum":{"type":"string","enum":["Innocent","Guilty","Unknown"]},"optionalString":{"type":"string"},"string":{"type":"string"},"int":{"type":"int"},"long":{"type":"long"},"bool":{"type":"boolean"},"double":{"type":"double"},"float":{"type":"float"},"any":{"type":"object"},"map":{"type":"object"},"listString":{"type":"array","items":{"type":"string"}},"arrayString":{"type":"array","items":{"type":"string"}},"arrayInteger":{"type":"array","items":{"type":"int"}},"arrayEnum":{"type":"array","enum":["Innocent","Guilty","Unknown"],"items":{"type":"string"}}},"required":["enum","optionalString","string","int","long","bool","double","float","any","map","listString","arrayString","arrayInteger","arrayEnum"],"description":"Function return type","type":"object"}"""
        )
    }

    @Test
    fun functionInit() {
        class SampleArguments
    }


    @Test
    fun parse() {
        assertFailsWith<ParseException> {
            sampleFunction.castReturn("")
        }

        assertFailsWith<ParseException> {
            sampleFunction.castArgs("")
        }

        assertEquals(sampleFunction.castArgs("{\"argsString\":\"foo\"}").argsString, "foo")
        assertEquals(sampleFunction.castArgs("{\"argsString\":\"foo\"}").argsLong, 0)

        assertEquals(sampleFunction.castArgs("{\"argsString\":\"foo\",\"argsLong\":1}").argsLong, 1)
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

            @FlappyField(description = "ignore it", subType = FieldType.STRING, optional = true)
            val arg2: List<String>?
        )

        class GetLatestLawsuitsReturn(
            @FlappyField
            val output: String
        )


        val lawGetLatestLawsuitsByPlaintiff = FlappyInvokeFunction(
            name = "getLatestLawsuitsByPlaintiff",
            description = "Get the latest lawsuits by plaintiff.",
            args = GetLatestLawsuitsArguments::class.java,
            returnType = GetLatestLawsuitsReturn::class.java,
            invoker = { args, _ -> GetLatestLawsuitsReturn(args.plaintiff) }
        )

        val lawAgent = FlappyBaseAgent(
            maxRetry = 3,
            inferenceLLM = Dummy(),
            functions = listOf(lawGetLatestLawsuitsByPlaintiff)
        )

        runBlocking {
            val result = lawGetLatestLawsuitsByPlaintiff.call("""{"plaintiff":"foo", "arg1":true}""", lawAgent)
            assertEquals(result.output, "foo")
        }

    }

}