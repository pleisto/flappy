package flappy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ReflectTest {
    @Test
    fun reflect() {
        data class Foo(val foo: String)

        val foo = Foo("abc")

        val value = readInstanceProperty<String>(foo, "foo")

        assertEquals(value, "abc")

        assertEquals(readInstanceProperty(foo, "foo"), "abc")


        assertFails {
            readInstanceProperty(foo, "bar")
        }

    }
}