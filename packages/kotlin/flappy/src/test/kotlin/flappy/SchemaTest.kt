package flappy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class SchemaTest {

  @Test
  fun schema() {
    data class Foo(val foo: String)

    val foo = Foo("abc")

    val value = foo.getField<String>("foo")

    assertEquals(value, "abc")

    assertEquals((foo.getField("foo")), "abc")

    assertFails {
      foo.getField("bar")
    }
  }
}
