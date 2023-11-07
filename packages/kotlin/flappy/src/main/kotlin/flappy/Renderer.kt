package flappy

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheException
import java.io.IOException
import java.io.StringWriter
import java.io.Writer


internal object Template {

  class NoEncodingMustacheFactory(s: String) : DefaultMustacheFactory(s) {
    override fun encode(value: String?, writer: Writer) {
      try {
        writer.write(value)
      } catch (e: IOException) {
        throw MustacheException(e)
      }
    }
  }

  private val mf = NoEncodingMustacheFactory("templates")

  fun render(file: String, map: Any = emptyMap<String, Any>()): String {
    mf.compile(file).let { mustache ->
      StringWriter().let { writer ->
        mustache.execute(writer, map).flush()
        return writer.toString()
      }
    }
  }
}
