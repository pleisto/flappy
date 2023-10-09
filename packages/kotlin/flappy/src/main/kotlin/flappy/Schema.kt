package flappy

import com.fasterxml.jackson.annotation.JsonInclude

internal sealed class FieldPropertyOrProperties(
  open val description: String?,
  open val type: String,
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
internal class FieldProperty(
  override val type: String,
  override val description: String? = null,
  val enum: List<String>? = null,
  val items: FieldPropertyOrProperties? = null
) : FieldPropertyOrProperties(description = description, type = type)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
internal class FieldProperties(
  val properties: Map<String, FieldPropertyOrProperties>,

  val required: List<String>? = null,

  override val description: String? = null
) : FieldPropertyOrProperties(description = description, type = FieldType.OBJECT.typeName)


internal class FunctionProperties(
  val args: FieldPropertyOrProperties,
  val returnType: FieldPropertyOrProperties
)

internal class FunctionParameters(
  val properties: FunctionProperties
) {
  val type = FieldType.OBJECT.typeName
}

internal class FunctionSchema(
  val name: String,
  val description: String,
  val parameters: FunctionParameters
)
