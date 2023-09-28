package flappy

import flappy.annotations.FlappyField
import javax.lang.model.type.NullType
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


    assertEquals(
      SampleArguments::class.java.buildFieldProperties("foo").asString(), """
        {"properties":{"argsLong":{"type":"long","description":"Long foo bar"}},"required":["argsLong"],"description":"foo","type":"object"}
    """.trimIndent()
    )


    class SampleOptional(
      @FlappyField(description = "long desc", optional = true)
      val optLong: Long?
    )

    assertEquals(
      SampleOptional::class.java.buildFieldProperties().asString(), """
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
      String::class.java.buildFieldProperties().asString(), """
        {"type":"string"}
    """.trimIndent()
    )

    assertEquals(
      Double::class.java.buildFieldProperties("double").asString(), """
        {"type":"double","description":"double"}
    """.trimIndent()
    )

    assertEquals(
      SampleEnum::class.java.buildFieldProperties("enum").asString(), """
        {"type":"string","description":"enum","enum":["Foo","Bar","Baz"]}
    """.trimIndent()
    )
  }

  @Test
  fun nullable() {
    assertFails {
      NullType::class.java.buildFieldProperties().asString()
    }

    assertFails {
      Nothing::class.java.buildFieldProperties().asString()
    }

    assertEquals(
      FlappyClass.Null::class.java.buildFieldProperties().asString(),
      """{"type":"null"}"""
    )

    assertEquals(
      jacksonMapper.readValue("null", FlappyClass.Null::class.java),
      null
    )

    assertEquals(
      jacksonMapper.readValue("{}", FlappyClass.Null::class.java) is FlappyClass.Null,
      true
    )


  }

  @Test
  fun getSubType() {
    class Bar(
      @FlappyField
      val bar: Any
    )

    class Foo(
      @FlappyField
      val foo: List<String>,

      @FlappyField
      val enum: List<SampleEnum>,

      @FlappyField
      val bar: Bar,

      @FlappyField
      val bars: List<Bar>
    )

    println(Foo::class.java.buildFieldProperties().asString())
  }

  @Test
  fun list1() {
    // https://stackoverflow.com/a/75213023/20030734
    assertFails {
      List::class.java.buildFieldProperties()
    }
    assertFails {
      listOf<String>()::class.java.buildFieldProperties()
    }

    val listKlass = arrayListOf<String>().javaClass
    assertFails {
      listKlass.buildFieldProperties()
    }

    assertEquals(jacksonMapper.readValue("""["1","2"]""", listKlass), listOf("1", "2"))
    assertEquals(jacksonMapper.readValue("[]", listKlass), listOf())
  }


  @Test
  fun nested() {
    class Baz(
      @FlappyField
      val baz: Long?
    )

    class Foo(
      @FlappyField
      val name: String,

      @FlappyField(optional = true)
      val e: SampleEnum?
    )

    class Bar(
      @FlappyField
      val foo: Foo,

      @FlappyField
      val a1: Array<Int>,

      @FlappyField
      val a2: Array<SampleEnum>,

      @FlappyField
      val baz: Array<Baz>
    )

    assertEquals(
      Bar::class.java.buildFieldProperties("desc").asString(), """
        {"properties":{"foo":{"properties":{"name":{"type":"string"},"e":{"type":"string","enum":["Foo","Bar","Baz"]}},"required":["name"],"type":"object"},"a1":{"type":"array","items":{"type":"int"}},"a2":{"type":"array","items":{"type":"string","enum":["Foo","Bar","Baz"]}},"baz":{"type":"array","items":{"properties":{"baz":{"type":"long"}},"required":["baz"],"type":"object"}}},"required":["foo","a1","a2","baz"],"description":"desc","type":"object"}
    """.trimIndent()
    )
  }
}
