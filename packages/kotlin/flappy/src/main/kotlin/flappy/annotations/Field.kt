package flappy.annotations

import flappy.AnyClass
import flappy.FieldMetadata


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented()
annotation class FlappyField(
  val description: String = "",
  val optional: Boolean = false,
)


internal fun AnyClass.flappyFieldMetadataList(): List<FieldMetadata> {
  return this.declaredFields.filter { it.isAnnotationPresent((FlappyField::class.java)) }
    .map {
      val annotation = it.getAnnotation(FlappyField::class.java)
      FieldMetadata(
        field = it,
        description = annotation.description,
        optional = annotation.optional,
      )
    }
}
