package me.schlaubi.mongoutils.cache

import java.io.Closeable

/**
 * Key-value cache that stores every entity that was put into it.
 * @property K the type of the key
 * @property V the type of the stored entities
 *
 * @see CacheableMongoEntity
 *
 * @author Michael Rittmeister
 * @since 1.0
 */
interface Cache<K : Any, V : CacheableMongoEntity<V>> : MutableMap<K, V>, Iterable<V>, Closeable {

    /**
     * A [MutableSet] containing all [MutableEntries][MutableMap.MutableEntry] for the cached entities
     */
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = toMap().entries

    /**
     * A [MutableSet] of all they keys there are entites cached for.
     */
    override val keys: MutableSet<K>
        get() = toMap().keys

    /**
     * @see Cache.getAll
     */
    override val values: MutableCollection<V>
        get() = getAll()

    /**
     * Contains the amount of cached entities.
     */
    override val size: Int
        get() = toMap().size

    /**
     * @return the cached value corresponding to the [key] or `null` if there is no entity cached.
     */
    override operator fun get(key: K): V?

    /**
     * Puts the entity [value] into the cache corresponding to its [key].
     * @return the value of the specified [key] which got overridden or `null` if there was no cached value.
     */
    operator fun set(key: K, value: V): V?

    /**
     * @see Cache.set
     */
    override fun put(key: K, value: V): V? = set(key, value)

    fun update(value: V)

    /**
     * Puts the entity [value][Pair.first] into the cache corresponding to its [key][Pair.second].
     */
    operator fun plusAssign(pair: Pair<K, V>) {
        set(pair.first, pair.second)
    }

    /**
     * Invalidates corresponding entity to the [key] so it needs to be fetched again.
     * If you use a [LoadingCache] the entity will get fetched automatically on the next call of [Cache.get]
     * If you use a normal [Cache] you need to [set][Cache.set] the value of the entity again
     */
    fun invalidate(key: K): V?

    fun invalidate(value: V): V?

    operator fun minusAssign(key: K) {
        invalidate(key)
    }

    /**
     * @see Cache.invalidate
     */
    override fun remove(key: K) = invalidate(key)

    /**
     * @see MutableMap.remove
     */
    override fun remove(key: K, value: V) = if (get(key) == null) false else remove(key).run { true }

    /**
     * @return a [MutableCollection] containing all cached elements
     */
    fun getAll(): MutableCollection<V>

    /**
     * @return whether there is a entity cached corresponding to the [key] or not.
     */
    operator fun contains(key: K): Boolean

    /**
     * @see Cache.contains
     */
    override fun containsKey(key: K) = contains(key)

    /**
     * Returns the iterator of [Cache.getAll]
     */
    override operator fun iterator() = getAll().iterator()

    /**
     * @return if there are cached entities or not.
     */
    override fun isEmpty() = toMap().isEmpty()

    /**
     * Clears the whole cache so every entites needs to get fetched again
     */
    fun invalidateAll()

    /**
     * @see Cache.invalidateAll
     */
    override fun clear() = invalidateAll()

    fun toMap(): MutableMap<K, V>
}

/**
 * Cache which does automatically fetch entites that are not cached yet.
 * @see Cache
 * @see me.schlaubi.mongoutils.providers.Accessor
 *
 * @author Michael Rittmeister
 * @since 1.0
 */
interface LoadingCache<K : Any, V : CacheableMongoEntity<V>> : Cache<K, V> {

    /**
     * @return the cached value corresponding to the [key].
     */
    // Override because of not null return type
    override fun get(key: K): V
}