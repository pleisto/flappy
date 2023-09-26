package flappy

import flappy.annotations.FlappyField
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class SchemaTest {

  @Test
  fun schema() {
    class SampleArguments(
      @FlappyField(description = "Long foo bar")
      val argsLong: Long
    )


    val schema = buildFieldProperties(SampleArguments::class.java, "foo")
    assertEquals(
      jacksonMapper.writeValueAsString(schema), """
        {"properties":{"argsLong":{"type":"long","description":"Long foo bar"}},"required":["argsLong"],"description":"foo","type":"object"}
    """.trimIndent()
    )


    class SampleOptional(
      @FlappyField(description = "long desc", optional = true)
      val optLong: Long?
    )

    val schema2 = buildFieldProperties(SampleOptional::class.java)
    assertEquals(
      jacksonMapper.writeValueAsString(schema2), """
        {"properties":{"optLong":{"type":"long","description":"long desc"}},"type":"object"}
    """.trimIndent()
    )
  }

  enum class SampleEnum {
    Foo, Bar, Baz
  }

  @Test
  fun literal() {
    assertEquals(
      jacksonMapper.writeValueAsString(buildFieldProperties(String::class.java)), """
        {"type":"string"}
    """.trimIndent()
    )

    assertEquals(
      jacksonMapper.writeValueAsString(buildFieldProperties(Double::class.java, "double")), """
        {"type":"double","description":"double"}
    """.trimIndent()
    )

    assertEquals(
      jacksonMapper.writeValueAsString(buildFieldProperties(SampleEnum::class.java, "enum")), """
        {"type":"string","description":"enum","enum":["Foo","Bar","Baz"]}
    """.trimIndent()
    )
  }

  @Test
  fun list() {
    // https://stackoverflow.com/a/75213023/20030734
    assertFails {
      buildFieldProperties(List::class.java)
    }
  }
}
