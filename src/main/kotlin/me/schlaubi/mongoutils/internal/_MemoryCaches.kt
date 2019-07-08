package me.schlaubi.mongoutils.internal

import me.schlaubi.mongoutils.cache.Cache
import me.schlaubi.mongoutils.cache.CacheableMongoEntity
import me.schlaubi.mongoutils.cache.LoadingCache
import me.schlaubi.mongoutils.providers.Accessor
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass

internal open class MemoryCache<K : Any, V : CacheableMongoEntity<V>> :
    Cache<K, V> {

    protected val storage = mutableMapOf<K, V>()

    override fun containsValue(value: V) = storage.containsValue(value)

    override fun putAll(from: Map<out K, V>) = storage.putAll(from)

    override fun get(key: K) = storage[key]

    override fun set(key: K, value: V) = storage.put(key, value)

    override fun invalidate(key: K) = storage.remove(key)

    override fun getAll(): MutableCollection<V> = storage.values

    override fun contains(key: K) = storage.contains(key)

    override fun iterator() = storage.values.iterator()

    override fun isEmpty() = storage.isEmpty()

    override fun invalidateAll() = storage.clear()

    override fun toMap(): MutableMap<K, V> = storage

    override fun close() {}

    override fun invalidate(value: V): V? {
        val entry = findByValue(value)?: return null
        storage.remove(entry.key)
        return entry.value
    }

    override fun update(value: V) {
        val entry = findByValue(value) ?: throw IllegalArgumentException("This entry does not exist")
        storage.replace(entry.key, value)
    }

    private fun findByValue(value: V) = storage.entries.firstOrNull { it.value == value }
}

internal open class LoadingMemoryCache<K : Any, V : CacheableMongoEntity<V>>(
    keyClazz: KClass<K>,
    private val accessor: Accessor<V, K>
) : MemoryCache<K, V>(), LoadingCache<K, V> {

    constructor(keyClazz: Class<K>, accessor: Accessor<V, K>): this(keyClazz.kotlin, accessor)

    private val creator = CacheClassModel(accessor.collection.documentClass.kotlin, keyClazz)

    override fun get(key: K): V {
        val savedEntity = super.get(key)
        return savedEntity ?: (accessor[key] ?: creator.create(key).also { set(key, it) })
    }

}
