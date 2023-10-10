package flappy

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jsonMapper

internal val kotlinModule = KotlinModule.Builder()
  .enable(KotlinFeature.StrictNullChecks)
  .build()

internal val jacksonMapper = jsonMapper {
  addModule(kotlinModule)
}


internal fun isInvalidJson(data: String) = !(data.startsWith("{") && data.endsWith("}"))

internal fun Any.asString(): String = jacksonMapper.writeValueAsString(this)

internal fun <T> String.castBy(klass: Class<T>): T {
  return try {
    jacksonMapper.readValue(this, klass)
  } catch (e: MismatchedInputException) {
    throw FlappyException.ParseException(e.message ?: e.originalMessage)
  }
}
