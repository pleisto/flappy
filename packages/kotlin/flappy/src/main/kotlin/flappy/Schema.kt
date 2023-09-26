package flappy

import com.fasterxml.jackson.annotation.JsonInclude
import flappy.annotations.FlappyField

class FieldSubTypeProperty(val type: String)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class FieldProperty(
  val type: String,
  val description: String? = null,
  val enum: List<String>? = null,
  val items: FieldSubTypeProperty? = null
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class FieldProperties(
  val properties: Map<String, FieldProperty>,

  val required: List<String>? = null,

  val description: String? = null
) {
  val type = FieldType.OBJECT.type
}

class FunctionProperties(
  val args: FieldProperties,
  val returnType: FieldProperties
)

class FunctionParameters(
  val properties: FunctionProperties
) {
  val type = FieldType.OBJECT.type
}

class FunctionSchema(
  val name: String,
  val description: String,
  val parameters: FunctionParameters
)

interface FieldSchema {
  fun size(): Int
  fun buildSchema(description: String? = null): FieldProperties
}

fun buildFieldSchema(klass: Class<*>): FieldSchema {
  val fields = FlappyField.flappyFieldMetadataList(klass)

  return FieldMetadataSchema(fields)
}

class FieldMetadataSchema(private val data: List<FieldMetadata>) : FieldSchema {
  override fun size() = data.size

  init {
    if (data.isEmpty()) throw CompileException("field is empty")
  }

  override fun buildSchema(description: String?): FieldProperties {
    return FieldProperties(
      properties = data.associate { Pair(it.name, it.toFieldProperty()) },
      required = data.filter { !it.optional }.map { it.name },
      description = description
    )
  }
}
