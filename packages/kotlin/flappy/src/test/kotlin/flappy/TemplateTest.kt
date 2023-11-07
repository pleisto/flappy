package flappy

import kotlin.test.Test
import kotlin.test.assertEquals

class TemplateTest {
  @Test
  fun ping() {
    assertEquals(Template.render("test/ping.mustache", mapOf("name" to "foo")), "foo pong")
  }
}
