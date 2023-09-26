package flappy

import java.lang.reflect.Field

enum class FieldType {
  STRING {
    override val type = "string"
  },
  FLOAT {
    override val type = "float"
  },
  BOOLEAN {
    override val type = "boolean"
  },
  DOUBLE {
    override val type = "double"
  },
  INTEGER {
    override val type = "int"
  },
  LONG {
    override val type = "long"
  },
  LIST {
    override val type = "array"
  },
  ENUM {
    override val type = "string"
  },
  OBJECT {
    override val type = "object"
  },

  NONE {
    override val type = "none"
  };

  abstract val type: String
}

fun getSingleFieldType(name: String, typeKlass: Class<*>): FieldType {
  if (typeKlass.isEnum) return FieldType.ENUM
  if (typeKlass.isArray) return FieldType.LIST

  return when (typeKlass) {
    String::class.java -> FieldType.STRING
    Boolean::class.java -> FieldType.BOOLEAN
    Boolean::class.javaObjectType -> FieldType.BOOLEAN

    Float::class.java -> FieldType.FLOAT
    Float::class.javaObjectType -> FieldType.FLOAT

    Double::class.java -> FieldType.DOUBLE
    Double::class.javaObjectType -> FieldType.DOUBLE

    Int::class.java -> FieldType.INTEGER
    Integer::class.java -> FieldType.INTEGER

    Long::class.java -> FieldType.LONG
    Long::class.javaObjectType -> FieldType.LONG

    List::class.java -> FieldType.LIST
    List::class.javaObjectType -> FieldType.LIST

    Map::class.java -> FieldType.OBJECT

    Object::class.java -> FieldType.OBJECT

    else -> throw FatalException("Field type `$typeKlass` is not supported for `$name`")
  }
}


data class FieldMetadata(
  val field: Field,
  val description: String,
  val optional: Boolean,
  val subType: FieldType
) {
  val modifier = field.modifiers
  val name: String = field.name
  private val type = field.type

  private val fieldType = getSingleFieldType(name, type)

  private val finalSubType: FieldType?
    get() {
      if (fieldType !== FieldType.LIST) return null

      if (type.isArray) {
        return getSingleFieldType(name, type.componentType)
      }

      return subType
    }

  private val enumValues: List<String>?
    get() {
      if (fieldType === FieldType.ENUM)
        return type.enumConstants.map { it.toString() }

      if (fieldType === FieldType.LIST && finalSubType === FieldType.ENUM)
        return type.componentType.enumConstants.map { it.toString() }

      return null
    }

  fun toFieldProperty() = FieldProperty(
    type = fieldType.type,
    description = description,
    enum = enumValues,
    items = finalSubType?.let { FieldSubTypeProperty(type = it.type) }
  )


  init {
    when (this.finalSubType) {
      FieldType.NONE -> throw CompileException("A `subType` should be passed in `List` type")
      FieldType.LIST -> throw CompileException("A `subType` cannot be a `List` type")
      else -> {}
    }
  }
}
