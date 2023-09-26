package flappy

import com.fasterxml.jackson.annotation.JsonInclude
import flappy.annotations.FlappyField

class FieldSubTypeProperty(val type: String)

sealed class FieldPropertyOrProperties(
  open val description: String?,
  open val type: String
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class FieldProperty(
  override val type: String,
  override val description: String? = null,
  val enum: List<String>? = null,
  val items: FieldSubTypeProperty? = null
) : FieldPropertyOrProperties(description = description, type = type)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class FieldProperties(
  val properties: Map<String, FieldProperty>,

  val required: List<String>? = null,

  override val description: String? = null
) : FieldPropertyOrProperties(description = description, type = FieldType.OBJECT.typeName)


class FunctionProperties(
  val args: FieldPropertyOrProperties,
  val returnType: FieldPropertyOrProperties
)

class FunctionParameters(
  val properties: FunctionProperties
) {
  val type = FieldType.OBJECT.typeName
}

class FunctionSchema(
  val name: String,
  val description: String,
  val parameters: FunctionParameters
)

interface FieldSchema {
  fun buildSchema(description: String? = null): FieldPropertyOrProperties
}

fun buildFieldProperties(klass: Class<*>, description: String? = null): FieldPropertyOrProperties {
  val field = getSingleFieldType(klass)
  if (field.isLiteral) return field.toFieldProperty(description)


  val fields = FlappyField.flappyFieldMetadataList(klass)

  return FieldMetadataSchema(fields).buildSchema(description)
}

class FieldMetadataSchema(private val data: List<FieldMetadata>) : FieldSchema {
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
