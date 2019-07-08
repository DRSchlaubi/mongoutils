@file:Suppress("unused")

package me.schlaubi.mongoutils.providers

import com.google.common.cache.CacheBuilder
import me.schlaubi.mongoutils.cache.*
import me.schlaubi.mongoutils.defaults.SnowflakeMongoEntity
import me.schlaubi.mongoutils.internal.LoadingGuavaCache
import me.schlaubi.mongoutils.internal.LoadingMemoryCache
import kotlin.reflect.KClass

fun <K : Any, T : CacheableMongoEntity<T>> Accessor<T, K>.asCache(keyClazz: KClass<K>): LoadingCache<K, T> =
    LoadingMemoryCache(keyClazz, this)

fun <K : Any, T : CacheableMongoEntity<T>> Accessor<T, K>.asCache(keyClazz: Class<K>): LoadingCache<K, T> =
    LoadingMemoryCache(keyClazz, this)

fun <T : SnowflakeMongoEntity<T>> Accessor<T, Long>.asCache(): LoadingSnowflakeCache<T> =
    SnowflakeLoadingMemoryCache(this)

fun <K : Any, T : CacheableMongoEntity<T>> Accessor<T, K>.asGuavaCache(
    keyClazz: KClass<K>, cacheBuilder: CacheBuilder<K, T>
): LoadingCache<K, T> = LoadingGuavaCache(keyClazz, this, cacheBuilder)

fun <K : Any, T : CacheableMongoEntity<T>> Accessor<T, K>.asGuavaCache(
    keyClazz: Class<K>, cacheBuilder: CacheBuilder<K, T>
): LoadingCache<K, T> = LoadingGuavaCache(keyClazz, this, cacheBuilder)

fun <T : SnowflakeMongoEntity<T>> Accessor<T, Long>.asGuavaCache(cacheBuilder: CacheBuilder<Long, T>): LoadingSnowflakeCache<T> =
    SnowflakeLoadingGuavaCache(this, cacheBuilder)
