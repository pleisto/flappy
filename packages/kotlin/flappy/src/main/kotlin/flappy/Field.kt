package flappy

import java.lang.reflect.Field

enum class FieldType {
  STRING {
    override val typeName = "string"
  },
  FLOAT {
    override val typeName = "float"
  },
  BOOLEAN {
    override val typeName = "boolean"
  },
  DOUBLE {
    override val typeName = "double"
  },
  INTEGER {
    override val typeName = "int"
  },
  LONG {
    override val typeName = "long"
  },
  LIST {
    override val typeName = "array"
  },
  ENUM {
    override val typeName = "string"
  },
  OBJECT {
    override val typeName = "object"
  },

  NONE {
    override val typeName = "none"
  };

  abstract val typeName: String
}

sealed class FieldTypeClass(open val klass: Class<*>) {
  open val isLiteral: Boolean = true
  abstract val type: FieldType

  open fun toFieldProperty(description: String? = null): FieldProperty {
    return FieldProperty(type = type.typeName, description = description)
  }

  class STRING(override val klass: Class<*>) : FieldTypeClass(klass) {
    override val type = FieldType.STRING
  }

  class FLOAT(override val klass: Class<*>) : FieldTypeClass(klass) {
    override val type = FieldType.FLOAT
  }

  class BOOLEAN(override val klass: Class<*>) : FieldTypeClass(klass) {
    override val type = FieldType.BOOLEAN
  }

  class DOUBLE(override val klass: Class<*>) : FieldTypeClass(klass) {
    override val type = FieldType.DOUBLE
  }

  class INTEGER(override val klass: Class<*>) : FieldTypeClass(klass) {
    override val type = FieldType.INTEGER
  }

  class LONG(override val klass: Class<*>) : FieldTypeClass(klass) {
    override val type = FieldType.LONG
  }

  class LIST(override val klass: Class<*>) : FieldTypeClass(klass) {
    override val isLiteral = false
    override val type = FieldType.LIST
  }

  class ENUM(override val klass: Class<*>) : FieldTypeClass(klass) {
    override val type = FieldType.ENUM

    private fun enumValues(): List<String> = klass.enumConstants.map { it.toString() }

    override fun toFieldProperty(description: String?): FieldProperty {
      return FieldProperty(type = type.typeName, description = description, enum = enumValues())
    }
  }

  class OBJECT(override val klass: Class<*>) : FieldTypeClass(klass) {
    override val isLiteral = false
    override val type = FieldType.OBJECT
  }

  class NONE(override val klass: Class<*>) : FieldTypeClass(klass) {
    override val type = FieldType.NONE
    override val isLiteral = false
  }

}

fun getSingleFieldType(typeKlass: Class<*>): FieldTypeClass {
  if (typeKlass.isEnum) return FieldTypeClass.ENUM(typeKlass)
  if (typeKlass.isArray) return FieldTypeClass.LIST(typeKlass)

  return when (typeKlass) {
    String::class.java -> FieldTypeClass.STRING(typeKlass)
    Boolean::class.java -> FieldTypeClass.BOOLEAN(typeKlass)
    Boolean::class.javaObjectType -> FieldTypeClass.BOOLEAN(typeKlass)

    Float::class.java -> FieldTypeClass.FLOAT(typeKlass)
    Float::class.javaObjectType -> FieldTypeClass.FLOAT(typeKlass)

    Double::class.java -> FieldTypeClass.DOUBLE(typeKlass)
    Double::class.javaObjectType -> FieldTypeClass.DOUBLE(typeKlass)

    Int::class.java -> FieldTypeClass.INTEGER(typeKlass)
    Integer::class.java -> FieldTypeClass.INTEGER(typeKlass)

    Long::class.java -> FieldTypeClass.LONG(typeKlass)
    Long::class.javaObjectType -> FieldTypeClass.LONG(typeKlass)

    List::class.java -> FieldTypeClass.LIST(typeKlass)
    List::class.javaObjectType -> FieldTypeClass.LIST(typeKlass)

    Map::class.java -> FieldTypeClass.OBJECT(typeKlass)

    Object::class.java -> FieldTypeClass.OBJECT(typeKlass)

    else -> FieldTypeClass.NONE(typeKlass)
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

  private val fieldType = getSingleFieldType(type)

  private val finalSubType: FieldType?
    get() {
      if (fieldType !is FieldTypeClass.LIST) return null

      if (type.isArray) {
        return getSingleFieldType(type.componentType).type
      }

      return subType
    }

  private val enumValues: List<String>?
    get() {
      if (fieldType is FieldTypeClass.ENUM)
        return type.enumConstants.map { it.toString() }

      if (fieldType is FieldTypeClass.LIST && finalSubType === FieldType.ENUM)
        return type.componentType.enumConstants.map { it.toString() }

      return null
    }

  fun toFieldProperty() = FieldProperty(
    type = fieldType.type.typeName,
    description = description,
    enum = enumValues,
    items = finalSubType?.let { FieldSubTypeProperty(type = it.typeName) }
  )


  init {
    if (this.fieldType is FieldTypeClass.NONE) {
      throw CompileException("`$name`: Type is not supported")
    }

    when (this.finalSubType) {
      FieldType.NONE -> throw CompileException("`$name`: A `subType` should be passed in `List` type")
      FieldType.LIST -> throw CompileException("`$name`: A `subType` cannot be a `List` type")
      else -> {}
    }
  }
}
