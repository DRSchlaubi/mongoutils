package me.schlaubi.mongoutils.providers

import com.mongodb.client.ClientSession
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.DeleteOptions
import com.mongodb.client.model.Filters
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.result.DeleteResult
import me.schlaubi.mongoutils.MongoEntity
import me.schlaubi.mongoutils.util.join
import org.bson.conversions.Bson
import java.util.concurrent.CompletionStage
import java.util.function.Consumer

/**
 * Way of dealing with [MongoCollection] while using the Mapper
 *
 * @author Michael Rittmeister
 * @since 1.0
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
interface Accessor<T : MongoEntity<T>, K> : Iterable<T> {

    /**
     * The [MongoCollection] containing the entites.
     */
    val collection: MongoCollection<T>

    /**
     * @return a [CompletionStage] containing the corresponding entity for the [key]
     */
    fun getAsync(key: K): CompletionStage<T>

    /**
     * @return the corresponding entity for the [key]
     */
    operator fun get(key: K): T? = join(getAsync(key))

    /**
     * @return a [CompletionStage] containing all the entities inside the [collection].
     */
    fun getAllAsync(): CompletionStage<List<T>>

    /**
     * @return all the entities inside the [collection].
     */
    fun getAll(): List<T> = join(getAllAsync())

    /**
     * @see MongoCollection.insertOne
     */
    fun insertAsync(instance: T, options: InsertOneOptions, clientSession: ClientSession): CompletionStage<Unit>

    /**
     * @see MongoCollection.insertOne
     */
    fun insertAsync(instance: T, options: InsertOneOptions): CompletionStage<Unit>

    /**
     * @see MongoCollection.insertOne
     */
    fun insertAsync(instance: T, clientSession: ClientSession): CompletionStage<Unit>

    /**
     * @see MongoCollection.insertOne
     */
    fun insertAsync(instance: T): CompletionStage<Unit>

    /**
     * @see MongoCollection.insertOne
     */
    fun insert(instance: T, options: InsertOneOptions, clientSession: ClientSession): Unit =
        join(insertAsync(instance, options, clientSession))

    /**
     * @see MongoCollection.insertOne
     */
    fun insert(instance: T, options: InsertOneOptions): Unit = join(insertAsync(instance, options))

    /**
     * @see MongoCollection.insertOne
     */
    fun insert(instance: T, clientSession: ClientSession): Unit = join(insertAsync(instance, clientSession))

    /**
     * @see MongoCollection.insertOne
     */
    fun insert(instance: T): Unit = join(insertAsync(instance))

    /**
     * @see MongoCollection.deleteOne
     */
    fun deleteAsync(key: K): CompletionStage<DeleteResult> = deleteAsync(Filters.eq(key))

    /**
     * @see MongoCollection.deleteMany
     */
    fun deleteAsync(filter: Bson, options: DeleteOptions, clientSession: ClientSession): CompletionStage<DeleteResult>

    /**
     * @see MongoCollection.deleteMany
     */
    fun deleteAsync(filter: Bson, options: DeleteOptions): CompletionStage<DeleteResult>

    /**
     * @see MongoCollection.deleteMany
     */
    fun deleteAsync(filter: Bson, clientSession: ClientSession): CompletionStage<DeleteResult>

    /**
     * @see MongoCollection.deleteMany
     */
    fun deleteAsync(filter: Bson): CompletionStage<DeleteResult>

    /**
     * @see MongoCollection.deleteMany
     */
    fun delete(filter: Bson, options: DeleteOptions, clientSession: ClientSession): DeleteResult =
        join(deleteAsync(filter, options, clientSession))

    /**
     * @see MongoCollection.deleteMany
     */
    fun delete(filter: Bson, options: DeleteOptions): DeleteResult = join(deleteAsync(filter, options))

    /**
     * @see MongoCollection.deleteMany
     */
    fun delete(filter: Bson, clientSession: ClientSession): DeleteResult = join(deleteAsync(filter, clientSession))

    /**
     * @see MongoCollection.deleteMany
     */
    fun delete(filter: Bson): DeleteResult = join(deleteAsync(filter))

    /**
     * @see MongoCollection.deleteOne
     */
    fun deleteAsync(key: K, options: DeleteOptions, clientSession: ClientSession): CompletionStage<DeleteResult> =
        deleteAsync(Filters.eq(key), options, clientSession)

    /**
     * @see MongoCollection.deleteOne
     */
    fun deleteAsync(key: K, options: DeleteOptions): CompletionStage<DeleteResult> =
        deleteAsync(Filters.eq(key), options)

    /**
     * @see MongoCollection.deleteOne
     */
    fun deleteAsync(key: K, clientSession: ClientSession): CompletionStage<DeleteResult> =
        deleteAsync(Filters.eq(key), clientSession)

    /**
     * @see MongoCollection.deleteOne
     */
    fun delete(key: K, options: DeleteOptions, clientSession: ClientSession): DeleteResult =
        join(deleteAsync(key, options, clientSession))

    /**
     * @see MongoCollection.deleteOne
     */
    fun delete(key: K, options: DeleteOptions): DeleteResult = join(deleteAsync(key, options))

    /**
     * @see MongoCollection.deleteOne
     */
    fun delete(key: K, clientSession: ClientSession): DeleteResult = join(deleteAsync(key, clientSession))

    /**
     * @see MongoCollection.deleteOne
     */
    fun delete(key: K): DeleteResult = join(deleteAsync(key))

    /**
     * Executes the [action] on every entity in the collection.
     * @see Accessor.getAll
     */
    override fun forEach(action: Consumer<in T>) = super.forEach { action.accept(it) }

    /**
     * Executes the [action] on every entity in the collection.
     * @see Accessor.getAllAsync
     *
     * @return a [CompletionStage] that completes when all tasks are done
     */
    fun forEachAsync(action: (T) -> Unit): CompletionStage<Void> =
        iteratorAsync().thenAcceptAsync { it.forEach(action) }

    /**
     * @see Accessor.getAllAsync
     * @return a [CompletionStage] containing an [Iterator] for all entities in the database.
     */
    fun iteratorAsync(): CompletionStage<Iterator<T>> = getAllAsync().thenApply { it.iterator() }

    operator fun contains(key: K): Boolean

    /**
     * @see Accessor.getAll
     * @return an [Iterator] for all entities in the database.
     */
    override operator fun iterator(): Iterator<T> = join(iteratorAsync())

}