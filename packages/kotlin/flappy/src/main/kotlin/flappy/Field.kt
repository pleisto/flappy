package flappy

import flappy.annotations.flappyFieldMetadataList
import java.lang.reflect.Field

sealed class FlappyClass {
  data object Null : FlappyClass()
  data object String : FlappyClass()
  data object Float : FlappyClass()
  data object Boolean : FlappyClass()
  data object Double : FlappyClass()
  data object Number : FlappyClass()
  data object Integer : FlappyClass()
  data object Long : FlappyClass()
  data object List : FlappyClass()
  data object Enum : FlappyClass()
  data object Object : FlappyClass()
  data object Unknown : FlappyClass()
}


enum class FieldType(val custom: AnyClass) {
  NULL(FlappyClass.Null.javaClass) {
    override val typeName = "null"
  },
  STRING(FlappyClass.String.javaClass) {
    override val typeName = "string"
  },
  FLOAT(FlappyClass.Float.javaClass) {
    override val typeName = "float"
  },
  BOOLEAN(FlappyClass.Boolean.javaClass) {
    override val typeName = "boolean"
  },
  DOUBLE(FlappyClass.Double.javaClass) {
    override val typeName = "double"
  },
  NUMBER(FlappyClass.Number.javaClass) {
    override val typeName = "number"
  },
  INTEGER(FlappyClass.Integer.javaClass) {
    override val typeName = "int"
  },
  LONG(FlappyClass.Long.javaClass) {
    override val typeName = "long"
  },
  LIST(FlappyClass.List.javaClass) {
    override val typeName = "array"
  },
  ENUM(FlappyClass.Enum.javaClass) {
    override val typeName = "string"
  },
  OBJECT(FlappyClass.Object.javaClass) {
    override val typeName = "object"
  },

  UNKNOWN(FlappyClass.Unknown.javaClass) {
    override val typeName = "object"
  };

  abstract val typeName: String
}

sealed class FieldTypeClass(open val klass: AnyClass) {
  open val isLiteral: Boolean = true
  abstract var type: FieldType

  companion object {
    fun from(type: FieldType, klass: AnyClass): FieldTypeClass {
      return when (type) {
        FieldType.NULL -> NULL(klass)
        FieldType.STRING -> STRING(klass)
        FieldType.FLOAT -> FLOAT(klass)
        FieldType.BOOLEAN -> BOOLEAN(klass)
        FieldType.DOUBLE -> DOUBLE(klass)
        FieldType.INTEGER -> INTEGER(klass)
        FieldType.LONG -> LONG(klass)
        FieldType.NUMBER -> NUMBER(klass)

        FieldType.ENUM -> ENUM(klass)
        FieldType.LIST -> LIST(klass)
        FieldType.OBJECT -> OBJECT(klass)
        FieldType.UNKNOWN -> UNKNOWN(klass)
      }
    }
  }

  open fun toFieldPropertyOrProperties(description: String? = null): FieldPropertyOrProperties {
    return FieldProperty(type = type.typeName, description = description)
  }

  class NULL(override val klass: AnyClass) : FieldTypeClass(klass) {
    override var type: FieldType = FieldType.NULL
  }

  class STRING(override val klass: AnyClass) : FieldTypeClass(klass) {
    override var type = FieldType.STRING
  }

  class FLOAT(override val klass: AnyClass) : FieldTypeClass(klass) {
    override var type = FieldType.FLOAT
  }

  class BOOLEAN(override val klass: AnyClass) : FieldTypeClass(klass) {
    override var type = FieldType.BOOLEAN
  }

  class DOUBLE(override val klass: AnyClass) : FieldTypeClass(klass) {
    override var type = FieldType.DOUBLE
  }

  class INTEGER(override val klass: AnyClass) : FieldTypeClass(klass) {
    override var type = FieldType.INTEGER
  }

  class NUMBER(override val klass: AnyClass) : FieldTypeClass(klass) {
    override var type = FieldType.NUMBER
  }

  class LONG(override val klass: AnyClass) : FieldTypeClass(klass) {
    override var type = FieldType.LONG
  }

  class LIST(override val klass: AnyClass) : FieldTypeClass(klass) {
    override var type = FieldType.LIST

    private fun subType() = klass.getSubType() ?: throw RuntimeException("subtype not found")

    override fun toFieldPropertyOrProperties(description: String?): FieldPropertyOrProperties {
      return FieldProperty(
        type = type.typeName,
        description = description,
        items = subType().toFieldPropertyOrProperties()
      )
    }
  }

  class ENUM(override val klass: AnyClass) : FieldTypeClass(klass) {
    override var type = FieldType.ENUM

    private fun enumValues(): List<String> = klass.enumConstants.map { it.toString() }

    override fun toFieldPropertyOrProperties(description: String?): FieldPropertyOrProperties {
      return FieldProperty(type = type.typeName, description = description, enum = enumValues())
    }
  }

  class OBJECT(override val klass: AnyClass) : FieldTypeClass(klass) {
    override val isLiteral = false
    override var type = FieldType.OBJECT
    private val metadataList = klass.flappyFieldMetadataList()

    override fun toFieldPropertyOrProperties(description: String?): FieldPropertyOrProperties {
      return FieldProperties(
        properties = metadataList.associate { Pair(it.name, it.toFieldProperty()) },
        required = metadataList.filter { !it.optional }.map { it.name },
        description = description
      )
    }
  }

  class UNKNOWN(override val klass: AnyClass) : FieldTypeClass(klass) {
    override var type = FieldType.UNKNOWN
    override val isLiteral = false

    override fun toFieldPropertyOrProperties(description: String?): FieldPropertyOrProperties {
      throw RuntimeException("not supported")
    }
  }

}


@JvmOverloads
fun AnyClass.buildFieldProperties(description: String? = null) =
  this.getSingleFieldType().toFieldPropertyOrProperties(description)


fun AnyClass.getSubType(): FieldTypeClass? {
  if (this.componentType !== null) return this.componentType.getSingleFieldType()

//  val typeClassifier = typeOf<T>().classifier
//  println(typeClassifier)
//  val firstParameter = klass.typeParameters.first()
//  val v = klass.genericSuperclass
//  println((v as ParameterizedType).actualTypeArguments[0].javaClass)
//  println(klass.genericSuperclass.javaClass)
//  println(firstParameter.javaClass)

  return null
}

fun AnyClass.getSingleFieldType(): FieldTypeClass {
  if (this.isEnum) return FieldTypeClass.ENUM(this)
  if (this.isArray) return FieldTypeClass.LIST(this)

  return when (this) {
    FlappyClass.Null::class.java -> FieldTypeClass.NULL(this)
    FlappyClass.Null::class.javaObjectType -> FieldTypeClass.NULL(this)

    String::class.java -> FieldTypeClass.STRING(this)
    Boolean::class.java -> FieldTypeClass.BOOLEAN(this)
    Boolean::class.javaObjectType -> FieldTypeClass.BOOLEAN(this)

    Number::class.java -> FieldTypeClass.NUMBER(this)
    Number::class.javaObjectType -> FieldTypeClass.NUMBER(this)

    Float::class.java -> FieldTypeClass.FLOAT(this)
    Float::class.javaObjectType -> FieldTypeClass.FLOAT(this)

    Double::class.java -> FieldTypeClass.DOUBLE(this)
    Double::class.javaObjectType -> FieldTypeClass.DOUBLE(this)

    Int::class.java -> FieldTypeClass.INTEGER(this)
    Integer::class.java -> FieldTypeClass.INTEGER(this)

    Long::class.java -> FieldTypeClass.LONG(this)
    Long::class.javaObjectType -> FieldTypeClass.LONG(this)

    ArrayList::class.java -> FieldTypeClass.LIST(this)
    ArrayList::class.javaObjectType -> FieldTypeClass.LIST(this)
    List::class.java -> FieldTypeClass.LIST(this)
    List::class.javaObjectType -> FieldTypeClass.LIST(this)
    Array::class.java -> FieldTypeClass.LIST(this)
    Array::class.javaObjectType -> FieldTypeClass.LIST(this)

    Map::class.java -> FieldTypeClass.OBJECT(this)
    Map::class.javaObjectType -> FieldTypeClass.OBJECT(this)

    Object::class.java -> FieldTypeClass.OBJECT(this)

    else -> {
      if (this.flappyFieldMetadataList().isNotEmpty()) return FieldTypeClass.OBJECT(this)
      FieldTypeClass.UNKNOWN(this)
    }
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

  private val fieldType = type.getSingleFieldType()

  private val finalSubType: FieldTypeClass?
    get() {
      if (fieldType !is FieldTypeClass.LIST) return null

      return type.getSubType() ?: FieldTypeClass.from(subType, type)
    }

  private val enumValues: List<String>?
    get() {
      if (fieldType is FieldTypeClass.ENUM)
        return type.enumConstants.map { it.toString() }

      return null
    }

  fun toFieldProperty(): FieldPropertyOrProperties {
    if (fieldType is FieldTypeClass.OBJECT) {
      return fieldType.toFieldPropertyOrProperties(description)
    }
    return FieldProperty(
      type = fieldType.type.typeName,
      description = description,
      enum = enumValues,
      items = finalSubType?.let { it.toFieldPropertyOrProperties() }
    )
  }


  init {
    if (this.fieldType is FieldTypeClass.UNKNOWN) {
      throw CompileException("`$name`: Type is not supported")
    }

    when (this.finalSubType?.type) {
      FieldType.UNKNOWN -> throw CompileException("`$name`: A `subType` should be passed in `List` type")
      FieldType.LIST -> throw CompileException("`$name`: A `subType` cannot be a `List` type")
      else -> {}
    }
  }
}
