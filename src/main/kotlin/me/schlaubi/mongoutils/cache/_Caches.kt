package me.schlaubi.mongoutils.cache

import java.io.Closeable

interface Cache<K : Any, V : CacheableMongoEntity<V>> : Iterable<V>, Closeable {

    operator fun get(key: K): V?

    operator fun set(key: K, value: V): V?

    operator fun plusAssign(pair: Pair<K, V>) {
        set(pair.first, pair.second)
    }

    fun invalidate(key: K): V?

    fun getAll(): MutableCollection<V>

    operator fun contains(key: K): Boolean

    override operator fun iterator(): Iterator<V>

}

interface LoadingCache<K : Any, V : CacheableMongoEntity<V>> : Cache<K, V> {
    override fun get(key: K): V
}