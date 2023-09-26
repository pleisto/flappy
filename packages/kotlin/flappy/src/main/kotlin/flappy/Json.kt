package flappy

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
