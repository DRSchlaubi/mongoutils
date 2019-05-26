package me.schlaubi.mongoutils.defaults
import me.schlaubi.mongoutils.cache.CacheableMongoEntity
import org.bson.codecs.pojo.annotations.BsonId

abstract class SnowflakeMongoEntity<T: SnowflakeMongoEntity<T>>(
    @BsonId val id: Long
) : CacheableMongoEntity<T>()