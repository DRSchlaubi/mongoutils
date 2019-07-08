package me.schlaubi.mongoutils.internal

import me.schlaubi.mongoutils.annotations.CacheConstructor
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

internal class CacheClassModel<K : Any, T : Any>(
    clazz: KClass<T>,
    keyClazz: KClass<K>
) {

    private val creator: KCallable<T>

    init {
        val constructor = if (clazz.findAnnotation<CacheConstructor>() != null) {
            clazz.primaryConstructor
                ?: throw IllegalArgumentException("Only classes with primary constructors can be annotated with @CacheConstructor!")
        } else {
            clazz.constructors.firstOrNull { it.findAnnotation<CacheConstructor>() != null }
                ?: with(clazz.staticFunctions.firstOrNull { it.findAnnotation<CacheConstructor>() != null }) {
                    if (this == null) {
                        throw IllegalArgumentException("Cacheable database entity needs a method/constructor annotated with @CacheConstructor")
                    }
                    if (this.returnType.jvmErasure != keyClazz) {
                        throw IllegalStateException("The creator method has to return an object which is type of the cached entity")
                    }
                    @Suppress("UNCHECKED_CAST")
                    this as KCallable<T>
                }
                ?: throw IllegalArgumentException("Cacheable database entity needs a method/constructor annotated with @CacheConstructor")
        }
        if (constructor.visibility != KVisibility.PUBLIC) {
            throw IllegalStateException("The @CacheConstructor is only allowed to have exactly one parameter!")
        }
        if (constructor.valueParameters.size != 1) {
            throw IllegalStateException("The @CacheConstructor is only allowed to have exactly one parameter!")
        }
        if (constructor.valueParameters.first().type.jvmErasure !=b keyClazz) {
            throw IllegalStateException("The @CacheConstructor's first parameter has to be the key!")
        }
        this.creator = constructor
    }

    fun create(key: K) = creator.call(key)

}