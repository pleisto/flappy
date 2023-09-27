package flappy.annotations

import flappy.AnyClass
import flappy.FieldMetadata
import flappy.FieldType


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented()
annotation class FlappyField(
  val description: String = "",
  val optional: Boolean = false,
  val subType: FieldType = FieldType.UNKNOWN
)


fun AnyClass.flappyFieldMetadataList(): List<FieldMetadata> {
  return this.declaredFields.filter { it.isAnnotationPresent((FlappyField::class.java)) }
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
