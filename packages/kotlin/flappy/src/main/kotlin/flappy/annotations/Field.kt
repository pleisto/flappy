package flappy.annotations

import flappy.FieldMetadata
import flappy.FieldType


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented()
annotation class FlappyField(
  val description: String = "",
  val optional: Boolean = false,
  val subType: FieldType = FieldType.NONE
) {
  companion object {
    fun flappyFieldMetadataList(klass: Class<*>): List<FieldMetadata> {
      return klass.declaredFields.filter { it.isAnnotationPresent((FlappyField::class.java)) }
        .map {
          val annotation = it.getAnnotation(FlappyField::class.java)
          FieldMetadata(
            field = it,
            description = annotation.description,
            optional = annotation.optional,
            subType = annotation.subType
          )
        }
    }
  }
}
