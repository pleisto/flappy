package flappy

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jsonMapper

val kotlinModule = KotlinModule.Builder()
  .enable(KotlinFeature.StrictNullChecks)
  .build()

val jacksonMapper = jsonMapper {
  addModule(kotlinModule)
}


fun isInvalidJson(data: String) = !(data.startsWith("{") && data.endsWith("}"))

fun Any.asString(): String = jacksonMapper.writeValueAsString(this)

fun <T> String.castBy(klass: Class<T>): T {
  return try {
    jacksonMapper.readValue(this, klass)
  } catch (e: MismatchedInputException) {
    throw ParseException(e.message ?: e.originalMessage)
  }
}
