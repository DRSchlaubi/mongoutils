package me.schlaubi.mongoutils.cache

import me.schlaubi.mongoutils.annotations.CacheConstructor
import me.schlaubi.mongoutils.providers.Accessor
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaConstructor
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmErasure

open class MemoryCache<K : Any, V : CacheableMongoEntity<V>> : Cache<K, V> {

    private val storage = mutableMapOf<K, V>()

    override fun get(key: K) = storage[key]

    override fun set(key: K, value: V) = storage.put(key, value)

    override fun invalidate(key: K) = storage.remove(key)

    override fun getAll(): MutableCollection<V> = storage.values

    override fun contains(key: K) = storage.contains(key)

    override fun iterator() = storage.values.iterator()

    override fun close() {
    }
}

open class LoadingMemoryCache<K : Any, V : CacheableMongoEntity<V>>(
    keyClazz: Class<K>,
    private val accessor: Accessor<V, K>
) :
    MemoryCache<K, V>(), LoadingCache<K, V> {

    private val constructor: KCallable<V>

    init {
        val clazz = accessor.collection.documentClass.kotlin
        val constructor = if (clazz.findAnnotation<CacheConstructor>() != null) {
            clazz.primaryConstructor
                ?: throw IllegalArgumentException("Only classes with primary constructors can be annotated with @CacheConstructor!")
        } else {
            val secondaryConstructor = clazz.constructors.firstOrNull { it.findAnnotation<CacheConstructor>() != null }
                ?: throw IllegalArgumentException("@CacheConstructor Annotation not found!")
            if (secondaryConstructor.visibility == KVisibility.PUBLIC) {
                secondaryConstructor
            } else {
                throw IllegalStateException("@CacheConstructor must be public")
            }
        }
        if (constructor.valueParameters.size != 1) {
            throw IllegalStateException("The @CacheConstructor is only allowed to have exactly one parameter!")
        }
        if (constructor.valueParameters.first().type.jvmErasure != keyClazz) {
            throw IllegalStateException("The @CacheConstructor's first parameter has to be the key!")
        }
        @Suppress("UNCHECKED_CAST")
        this.constructor = constructor
    }

    override fun get(key: K): V {
        val savedEntity = super.get(key)
        return if (savedEntity == null) {
            val databaseEntity = accessor[key] ?: constructor.call(key).also { accessor.insert(it) }
            set(key, databaseEntity)
            databaseEntity
        } else {
            savedEntity
        }
    }
}
