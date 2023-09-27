package flappy

import com.fasterxml.jackson.annotation.JsonInclude

sealed class FieldPropertyOrProperties(
  open val description: String?,
  open val type: String,
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class FieldProperty(
  override val type: String,
  override val description: String? = null,
  val enum: List<String>? = null,
  val items: FieldPropertyOrProperties? = null
) : FieldPropertyOrProperties(description = description, type = type)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class FieldProperties(
  val properties: Map<String, FieldPropertyOrProperties>,

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
