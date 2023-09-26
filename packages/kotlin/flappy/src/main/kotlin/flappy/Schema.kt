package flappy

import com.fasterxml.jackson.annotation.JsonInclude

enum class FieldSchemaType {
  ARGUMENTS {
    override val description = "Function arguments"

  },
  RETURN_TYPE {
    override val description = "Function return type"
  },
  BASE_STEP {
    override val description = "Base step."
  }
  ;


  abstract val description: String
}

class FieldSubTypeProperty(val type: String)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class FieldProperty(
  val type: String,
  val description: String? = null,
  val enum: List<String>? = null,
  val items: FieldSubTypeProperty? = null
)

class FieldProperties(
  val properties: Map<String, FieldProperty>,

  val required: List<String>? = null,

  val description: String
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

class FieldMetadataSchema(private val data: List<FieldMetadata>) {
  fun size() = data.size

  init {
    if (data.isEmpty()) throw CompileException("field is empty")
  }

  fun buildSchema(type: FieldSchemaType): FieldProperties {
    return FieldProperties(
      properties = data.associate { Pair(it.name, it.toFieldProperty()) },
      required = data.filter { !it.optional }.map { it.name },
      description = type.description
    )
  }
}
