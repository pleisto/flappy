package flappy

import flappy.annotations.flappyFieldMetadataList
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

sealed class FlappyClass {
  data object Null : FlappyClass()
}


enum class FieldType {
  NULL {
    override val typeName = "null"
  },
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
  NUMBER {
    override val typeName = "number"
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

  UNKNOWN {
    override val typeName = "object"
  };

  abstract val typeName: String
}

sealed class FieldTypeClass(open val type: Type) {
  fun buildKlass() = type.getRawClass()
  open val isLiteral: Boolean = true
  abstract var fieldType: FieldType

  open fun toFieldPropertyOrProperties(description: String? = null): FieldPropertyOrProperties {
    return FieldProperty(type = fieldType.typeName, description = description)
  }

  open fun enumValues(): List<String>? = null

  class NULL(override val type: Type) : FieldTypeClass(type) {
    override var fieldType: FieldType = FieldType.NULL
  }

  class STRING(override val type: Type) : FieldTypeClass(type) {
    override var fieldType = FieldType.STRING
  }

  class FLOAT(override val type: Type) : FieldTypeClass(type) {
    override var fieldType = FieldType.FLOAT
  }

  class BOOLEAN(override val type: Type) : FieldTypeClass(type) {
    override var fieldType = FieldType.BOOLEAN
  }

  class DOUBLE(override val type: Type) : FieldTypeClass(type) {
    override var fieldType = FieldType.DOUBLE
  }

  class INTEGER(override val type: Type) : FieldTypeClass(type) {
    override var fieldType = FieldType.INTEGER
  }

  class NUMBER(override val type: Type) : FieldTypeClass(type) {
    override var fieldType = FieldType.NUMBER
  }

  class LONG(override val type: Type) : FieldTypeClass(type) {
    override var fieldType = FieldType.LONG
  }

  class LIST(override val type: Type) : FieldTypeClass(type) {
    override var fieldType = FieldType.LIST

    override fun toFieldPropertyOrProperties(description: String?): FieldPropertyOrProperties {
      val subType = type.getSubType()
      if (subType === null) throw RuntimeException("subtype not found $type")
      return FieldProperty(
        type = fieldType.typeName,
        description = description,
        items = subType.getRawFieldType().toFieldPropertyOrProperties()
      )
    }
  }

  class ENUM(override val type: Type) : FieldTypeClass(type) {
    override var fieldType = FieldType.ENUM

    override fun enumValues(): List<String> = buildKlass().enumConstants.map { it.toString() }

    override fun toFieldPropertyOrProperties(description: String?): FieldPropertyOrProperties {
      return FieldProperty(type = fieldType.typeName, description = description, enum = enumValues())
    }
  }

  class OBJECT(override val type: Type) : FieldTypeClass(type) {
    override val isLiteral = false
    override var fieldType = FieldType.OBJECT
    private val metadataList = buildKlass().flappyFieldMetadataList()

    override fun toFieldPropertyOrProperties(description: String?): FieldPropertyOrProperties {
      return FieldProperties(
        properties = metadataList.associate { Pair(it.name, it.toFieldProperty()) },
        required = metadataList.filter { !it.optional }.map { it.name },
        description = description
      )
    }
  }

  class UNKNOWN(override val type: Type) : FieldTypeClass(type) {
    override var fieldType = FieldType.UNKNOWN
    override val isLiteral = false

    override fun toFieldPropertyOrProperties(description: String?): FieldPropertyOrProperties {
      throw RuntimeException("not supported")
    }
  }
}

fun AnyClass.getFlappyFieldType(type: Type): FieldTypeClass {
  if (this.isEnum) return FieldTypeClass.ENUM(type)
  if (this.isArray) return FieldTypeClass.LIST(type)

  return when (this) {
    FlappyClass.Null::class.java -> FieldTypeClass.NULL(type)
    FlappyClass.Null::class.javaObjectType -> FieldTypeClass.NULL(type)

    String::class.java -> FieldTypeClass.STRING(type)
    Boolean::class.java -> FieldTypeClass.BOOLEAN(type)
    Boolean::class.javaObjectType -> FieldTypeClass.BOOLEAN(type)

    Number::class.java -> FieldTypeClass.NUMBER(type)
    Number::class.javaObjectType -> FieldTypeClass.NUMBER(type)

    Float::class.java -> FieldTypeClass.FLOAT(type)
    Float::class.javaObjectType -> FieldTypeClass.FLOAT(type)

    Double::class.java -> FieldTypeClass.DOUBLE(type)
    Double::class.javaObjectType -> FieldTypeClass.DOUBLE(type)

    Int::class.java -> FieldTypeClass.INTEGER(type)
    Integer::class.java -> FieldTypeClass.INTEGER(type)

    Long::class.java -> FieldTypeClass.LONG(type)
    Long::class.javaObjectType -> FieldTypeClass.LONG(type)

    List::class.java -> FieldTypeClass.LIST(type)
    List::class.javaObjectType -> FieldTypeClass.LIST(type)
    ArrayList::class.java -> FieldTypeClass.LIST(type)
    ArrayList::class.javaObjectType -> FieldTypeClass.LIST(type)
    Array::class.java -> FieldTypeClass.LIST(type)
    Array::class.javaObjectType -> FieldTypeClass.LIST(type)
    java.util.ArrayList::class.java -> FieldTypeClass.LIST(type)
    java.util.ArrayList::class.javaObjectType -> FieldTypeClass.LIST(type)

    Map::class.java -> FieldTypeClass.OBJECT(type)
    Map::class.javaObjectType -> FieldTypeClass.OBJECT(type)

    Object::class.java -> FieldTypeClass.OBJECT(type)

    else -> {
      if (this.flappyFieldMetadataList().isNotEmpty()) return FieldTypeClass.OBJECT(type)
      FieldTypeClass.UNKNOWN(type)
    }
  }
}

@JvmOverloads
fun Type.buildFieldProperties(description: String? = null) =
  this.getRawFieldType().toFieldPropertyOrProperties(description)

fun Type.getRawClass(): AnyClass =
  when (this) {
    is AnyClass -> (this)
    is ParameterizedType -> this.rawType.getRawClass()
    else -> throw FatalException("invalid type ${this.typeName}")
  }

fun Type.getRawFieldType() = this.getRawClass().getFlappyFieldType(this)


fun Type.getSubType(): Type? {
  return when (this) {
    is AnyClass -> if (this.isArray) this.componentType else null
    is ParameterizedType -> {
      when (this.rawType) {
        List::class.java -> return this.actualTypeArguments[0]
        List::class.javaObjectType -> return this.actualTypeArguments[0]
        ArrayList::class.java -> return this.actualTypeArguments[0]
        ArrayList::class.javaObjectType -> return this.actualTypeArguments[0]
        Array::class.java -> return this.actualTypeArguments[0]
        Array::class.javaObjectType -> return this.actualTypeArguments[0]

        java.util.ArrayList::class.java -> return this.actualTypeArguments[0]
        java.util.ArrayList::class.javaObjectType -> return this.actualTypeArguments[0]
        else -> return null
      }
    }

    else -> throw FatalException("invalid type ${this.typeName}")
  }
}


data class FieldMetadata(
  val field: Field,
  val description: String,
  val optional: Boolean,
) {
  val name: String = field.name
  private val type = field.genericType

  private val fieldType = type.getRawFieldType()
  private val subType = type.getSubType()

  fun toFieldProperty() = fieldType.toFieldPropertyOrProperties(description)

  init {
    if (this.fieldType is FieldTypeClass.UNKNOWN) {
      throw CompileException("`$name`: Type is not supported")
    }

    if (subType !== null) {
      if (subType.getRawFieldType() is FieldTypeClass.UNKNOWN) {
        throw CompileException("`$name` - `$subType`: sub type is not supported")
      }
    }
  }
}
