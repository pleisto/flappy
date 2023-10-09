package flappy

import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

//https://stackoverflow.com/a/35539628/20030734
//@Suppress("UNCHECKED_CAST")
//fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
//  val property = instance::class.members
//    // don't cast here to <Any, R>, it would succeed silently
//    .first { it.name == propertyName } as KProperty1<Any, *>
//  // force a invalid cast exception if incorrect type here
//  return property.get(instance) as R
//}


//https://stackoverflow.com/a/56115232/20030734
internal inline fun <reified T> Any.getField(fieldName: String): T {
  this::class.memberProperties.forEach { kCallable ->
    kCallable.isAccessible = true
    if (fieldName == kCallable.name) {
      return kCallable.getter.call(this) as T
    }
  }
  throw FlappyException.FieldNotFoundException("$fieldName not found")
}
