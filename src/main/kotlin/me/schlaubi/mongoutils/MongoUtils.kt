package me.schlaubi.mongoutils

import com.mongodb.client.MongoClient
import me.schlaubi.mongoutils.providers.Accessor
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.Convention
import java.io.Closeable
import java.util.concurrent.ExecutorService
import kotlin.reflect.KClass

/**
 * MongoUtils.
 *
 * @author Michael Rittmeister
 * @since 1.0
 */
@Suppress("UNUSED")
interface MongoUtils : Closeable {

    /**
     * The [MongoClient] used by this instance.
     */
    val client: MongoClient

    /**
     * Creates a new [Accessor] for entities of [clazz].
     * @return the [Accessor]
     */
    fun <T : MongoEntity<T>, K : Any> createAccessor(clazz: KClass<T>): Accessor<T, K>

    /**
     * Creates a new [Accessor] for entities of [clazz].
     * @return the [Accessor]
     */
    fun <T : MongoEntity<T>, K : Any> createAccessor(clazz: Class<T>): Accessor<T, K> = createAccessor(clazz.kotlin)

    /**
     * Creates a new [Accessor] for entities of [clazz] and the additional [codecRegistry] using [org.bson.codecs.configuration.CodecRegistries.fromRegistries].
     * @return the [Accessor]
     */
    fun <T : MongoEntity<T>, K : Any> createAccessor(clazz: KClass<T>, codecRegistry: CodecRegistry): Accessor<T, K>

    /**
     * Creates a new [Accessor] for entities of [clazz] and the additional [codecRegistry] using [org.bson.codecs.configuration.CodecRegistries.fromRegistries].
     * @return the [Accessor]
     */
    fun <T : MongoEntity<T>, K : Any> createAccessor(clazz: Class<T>, codecRegistry: CodecRegistry): Accessor<T, K> =
        createAccessor(clazz.kotlin, codecRegistry)

    /**
     * Creates a new [Accessor] for entities of [clazz] using [pojoConventions] and the additional [codecRegistry] using [org.bson.codecs.configuration.CodecRegistries.fromRegistries].
     * @return the [Accessor]
     */
    fun <T : MongoEntity<T>, K : Any> createAccessor(
        clazz: KClass<T>,
        codecRegistry: CodecRegistry,
        pojoConventions: List<Convention>?
    ): Accessor<T, K>

    /**
     * Creates a new [Accessor] for entities of [clazz] using [pojoConventions] and the additional [codecRegistry] using [org.bson.codecs.configuration.CodecRegistries.fromRegistries].
     * @return the [Accessor]
     */
    fun <T : MongoEntity<T>, K : Any> createAccessor(
        clazz: Class<T>,
        codecRegistry: CodecRegistry,
        pojoConventions: List<Convention>?
    ): Accessor<T, K> = createAccessor(clazz.kotlin, codecRegistry, pojoConventions)

    /**
     * Creates a new [Accessor] for entities of [clazz] using [pojoConventions], the [executorService] and the additional [codecRegistry] using [org.bson.codecs.configuration.CodecRegistries.fromRegistries].
     * @return the [Accessor]
     */
    fun <T : MongoEntity<T>, K : Any> createAccessor(
        clazz: KClass<T>,
        codecRegistry: CodecRegistry,
        pojoConventions: List<Convention>?,
        executorService: ExecutorService
    ): Accessor<T, K>

    /**
     * Creates a new [Accessor] for entities of [clazz] using [pojoConventions], the [executorService] and the additional [codecRegistry] using [org.bson.codecs.configuration.CodecRegistries.fromRegistries].
     * @return the [Accessor]
     */
    fun <T : MongoEntity<T>, K : Any> createAccessor(
        clazz: Class<T>,
        codecRegistry: CodecRegistry,
        pojoConventions: List<Convention>?,
        executorService: ExecutorService
    ): Accessor<T, K> =
        createAccessor(clazz.kotlin, codecRegistry, pojoConventions, executorService)

    /**
     * Closes all mappers.
     */
    override fun close()

    companion object {
        /**
         * Creates a new [MongoUtilsBuilder] using the [client].
         */
        @JvmStatic
        fun builder(client: MongoClient): MongoUtilsBuilder {
            return MongoUtilsBuilder(client)
        }
    }

}