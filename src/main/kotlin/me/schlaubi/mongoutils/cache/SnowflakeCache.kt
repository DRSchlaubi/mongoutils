package me.schlaubi.mongoutils.cache

import me.schlaubi.mongoutils.defaults.SnowflakeMongoEntity

interface SnowflakeCache<V : SnowflakeMongoEntity<V>> : Cache<Long, V> {

    fun set(instance: V) = set(instance.id, instance)

    fun plusAssign(instance: V) {
        super.plusAssign(instance.id to instance)
    }
}