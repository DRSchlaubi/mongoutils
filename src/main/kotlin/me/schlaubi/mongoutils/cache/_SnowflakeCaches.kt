@file:Suppress("unused")

package me.schlaubi.mongoutils.cache

import com.google.common.cache.CacheBuilder
import me.schlaubi.mongoutils.defaults.SnowflakeMongoEntity
import me.schlaubi.mongoutils.internal.GuavaCache
import me.schlaubi.mongoutils.internal.LoadingGuavaCache
import me.schlaubi.mongoutils.internal.LoadingMemoryCache
import me.schlaubi.mongoutils.internal.MemoryCache
import me.schlaubi.mongoutils.providers.Accessor

/**
 * Modification of [Cache] with some extra helpers for [SnowflakeMongoEntity]
 * @see Cache
 * @see SnowflakeMongoEntity
 *
 * @author Michael Rittmeister
 * @since 1.0
 */
interface SnowflakeCache<V : SnowflakeMongoEntity<V>> : Cache<Long, V> {

    /**
     * Saves the [entity][instance] to the cache.
     */
    fun set(instance: V) = set(instance.id, instance)

    /**
     * Saves the [entity][instance] to the cache.
     * @see SnowflakeCache.set
     */
    operator fun plusAssign(instance: V) {
        set(instance)
    }

}

/**
 * Modification of [LoadingCache] with some extra helpers for [SnowflakeMongoEntity]
 * @see LoadingCache
 * @see SnowflakeMongoEntity
 *
 * @author Michael Rittmeister
 * @since 1.0
 */
interface LoadingSnowflakeCache<V : SnowflakeMongoEntity<V>> : SnowflakeCache<V>, LoadingCache<Long, V>

internal class SnowflakeMemoryCache<T : SnowflakeMongoEntity<T>> : MemoryCache<Long, T>(), SnowflakeCache<T>

internal class SnowflakeLoadingMemoryCache<T : SnowflakeMongoEntity<T>>(accessor: Accessor<T, Long>) :
    LoadingMemoryCache<Long, T>(Long::class.java, accessor), LoadingSnowflakeCache<T>

internal class SnowflakeGuavaCache<T : SnowflakeMongoEntity<T>>(cacheBuilder: CacheBuilder<Long, T>) :
    GuavaCache<Long, T>(cacheBuilder), SnowflakeCache<T>

internal class SnowflakeLoadingGuavaCache<T : SnowflakeMongoEntity<T>>(accessor: Accessor<T, Long>, cacheBuilder: CacheBuilder<Long, T>) :
    LoadingGuavaCache<Long, T>(Long::class.java, accessor, cacheBuilder), LoadingSnowflakeCache<T>