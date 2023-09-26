package flappy

import com.fasterxml.jackson.annotation.JsonInclude
import flappy.annotations.FlappyField

class FieldSubTypeProperty(val type: String)


sealed interface FieldPropertyOrProperties {
  val description: String?
  val type: String
}

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class FieldProperty(
  override val type: String,
  override val description: String? = null,
  val enum: List<String>? = null,
  val items: FieldSubTypeProperty? = null
) : FieldPropertyOrProperties

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class FieldProperties(
  val properties: Map<String, FieldProperty>,

  val required: List<String>? = null,

  override val description: String? = null
) : FieldPropertyOrProperties {
  override val type = FieldType.OBJECT.type
}


class FunctionProperties(
  val args: FieldPropertyOrProperties,
  val returnType: FieldPropertyOrProperties
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
  fun buildSchema(description: String? = null): FieldPropertyOrProperties
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

  override fun buildSchema(description: String?): FieldPropertyOrProperties {
    return FieldProperties(
      properties = data.associate { Pair(it.name, it.toFieldProperty()) },
      required = data.filter { !it.optional }.map { it.name },
      description = description
    )
  }
}
