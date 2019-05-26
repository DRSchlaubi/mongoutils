package me.schlaubi.mongoutils.cache

import me.schlaubi.mongoutils.defaults.SnowflakeMongoEntity
import me.schlaubi.mongoutils.providers.Accessor

class SnowflakeMemoryCache<T : SnowflakeMongoEntity<T>> : MemoryCache<Long, T>()

class SnowflakeLoadingMemoryCache<T : SnowflakeMongoEntity<T>>(accessor: Accessor<T, Long>) :
    LoadingMemoryCache<Long, T>(Long::class.java, accessor)