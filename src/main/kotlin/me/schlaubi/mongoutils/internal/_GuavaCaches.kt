package me.schlaubi.mongoutils.internal

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import me.schlaubi.mongoutils.cache.Cache
import me.schlaubi.mongoutils.cache.CacheableMongoEntity
import me.schlaubi.mongoutils.cache.LoadingCache
import me.schlaubi.mongoutils.providers.Accessor
import java.lang.UnsupportedOperationException
import kotlin.reflect.KClass

internal open class GuavaCache<K : Any, V : CacheableMongoEntity<V>>(
    cacheBuilder: CacheBuilder<K, V>
) : Cache<K, V> {
    @Suppress("LeakingThis")
    protected val cache: com.google.common.cache.Cache<K, V> = initCache(cacheBuilder)

    override val size: Int
        get() = cache.size().toInt()

    override fun invalidateAll() = cache.invalidateAll()

    override fun containsValue(value: V) = cache.asMap().containsValue(value)

    override fun putAll(from: Map<out K, V>) = cache.putAll(from)

    protected open fun initCache(
        cacheBuilder: CacheBuilder<K, V>
    ): com.google.common.cache.Cache<K, V> = cacheBuilder.build<K, V>()

    override fun get(key: K): V? = cache.get(key) { null }

    override fun set(key: K, value: V): Nothing? = cache.put(key, value).run { null }

    override fun invalidate(key: K): Nothing? = cache.invalidate(key).run { null }

    override fun getAll(): MutableCollection<V> = cache.asMap().values

    override fun contains(key: K) = cache.getIfPresent(key) != null

    override fun iterator() = getAll().iterator()

    override fun toMap(): MutableMap<K, V> = cache.asMap()

    override fun close() {
    }

    override fun invalidate(value: V): V? {
        val entry = findByValue(value) ?: return null
        cache.invalidate(entry.key)
        return null
    }

    override fun update(value: V) = throw UnsupportedOperationException("Use invalidate instead")

    private fun findByValue(value: V) = cache.asMap().entries.firstOrNull { it.value == value }


}

internal open class LoadingGuavaCache<K : Any, V : CacheableMongoEntity<V>>(
    keyClazz: KClass<K>,
    private val accessor: Accessor<V, K>,
    cacheBuilder: CacheBuilder<K, V>
) : GuavaCache<K, V>(cacheBuilder), LoadingCache<K, V> {

    constructor(
        keyClazz: Class<K>,
        accessor: Accessor<V, K>,
        cacheBuilder: CacheBuilder<K, V>
    ) : this(keyClazz.kotlin, accessor, cacheBuilder)

    private val creator = CacheClassModel(accessor.collection.documentClass.kotlin, keyClazz)

    override fun get(key: K) = (cache as com.google.common.cache.LoadingCache<K, V>)[key]

    override fun initCache(cacheBuilder: CacheBuilder<K, V>): com.google.common.cache.Cache<K, V> {
        return cacheBuilder
            .build(object : CacheLoader<K, V>() {
                override fun load(key: K): V {
                    return (accessor[key] ?: creator.create(key).also { set(key, it) })
                }
            })
    }

}
