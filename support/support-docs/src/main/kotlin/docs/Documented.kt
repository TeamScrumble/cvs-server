package docs

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
annotation class Documented(
    val summary: String,
    val description: String,
    val request: KClass<*> = Nothing::class,
    val response: KClass<*> = Nothing::class,
)
