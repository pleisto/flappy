package flappy

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import java.io.IOException


internal val kotlinModule = KotlinModule.Builder()
  .enable(KotlinFeature.StrictNullChecks)
  .build()

internal val jacksonMapper = jsonMapper {
  addModule(kotlinModule)
}


internal class CustomPrettyPrinter : DefaultPrettyPrinter {
  constructor() {
    val indenter: Indenter = DefaultIndenter("  ", System.getProperty("line.separator"))
    indentObjectsWith(indenter)
    indentArraysWith(indenter)
    _objectFieldValueSeparatorWithSpaces = ": "
  }

  private constructor(pp: CustomPrettyPrinter) : super(pp)

  @Throws(IOException::class)
  override fun writeEndArray(g: JsonGenerator, nrOfValues: Int) {
    if (!_arrayIndenter.isInline) {
      --_nesting
    }
    if (nrOfValues > 0) {
      _arrayIndenter.writeIndentation(g, _nesting)
    }
    g.writeRaw(']')
  }

  override fun createInstance(): DefaultPrettyPrinter {
    return CustomPrettyPrinter(this)
  }
}

internal val customPrettyPrinter = CustomPrettyPrinter()

internal fun buildYamlFactory(): YAMLFactory {
  val loaderOptions = LoaderOptions()
  val dumperOptions = DumperOptions()

  dumperOptions.isExplicitStart = true
  dumperOptions.isExplicitEnd = true

  return YAMLFactory.builder()
    .loaderOptions(loaderOptions)
    .dumperOptions(dumperOptions)
    .build()
}

var yamlMapper: ObjectMapper = YAMLMapper(buildYamlFactory())

internal fun isInvalidJson(data: String) = !(data.startsWith("{") && data.endsWith("}"))

internal fun Any.asJSON(prettyPrint: Boolean = false): String {
  return if (prettyPrint) {
    jacksonMapper.writer(customPrettyPrinter).writeValueAsString(this)
  } else {
    jacksonMapper.writeValueAsString(this)
  }
}


internal fun Any.asYAML(): String {
  return yamlMapper.writeValueAsString(this)
}


internal fun <T> String.castBy(klass: Class<T>): T {
  return try {
    jacksonMapper.readValue(this, klass)
  } catch (e: MismatchedInputException) {
    throw FlappyException.ParseException(e.message ?: e.originalMessage)
  }
}
