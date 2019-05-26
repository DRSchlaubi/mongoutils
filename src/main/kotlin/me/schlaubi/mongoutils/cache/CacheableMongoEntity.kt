package me.schlaubi.mongoutils.cache

import me.schlaubi.mongoutils.MongoEntity

abstract class CacheableMongoEntity<T : CacheableMongoEntity<T>>: MongoEntity<T>()
