package me.schlaubi.mongoutils

import com.mongodb.client.ClientSession
import com.mongodb.client.model.DeleteOptions
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import me.schlaubi.mongoutils.internal.Mapper
import me.schlaubi.mongoutils.util.join
import org.bson.codecs.pojo.annotations.BsonIgnore
import java.util.concurrent.CompletionStage

/**
 * Class which represents an entity saved oin a [com.mongodb.client.MongoCollection].
 * **Important:** Entity classes must be annotated with [me.schlaubi.mongoutils.annotations.Collection]
 *
 * Example usage:
 * ```Java
 * @Collection(name = "Users", database = "test")
 * public class User extends MongoEntity<User> {
 *  // POJO object
 * }```
 *
 * @property T the type of the entity
 *
 * @see me.schlaubi.mongoutils.annotations.Collection
 *
 * @author Michael Rittmeister
 * @since 1.0
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class MongoEntity<T : MongoEntity<T>> {

    @BsonIgnore
    lateinit var mapper: Mapper<T>

    /**
     * Inserts an entity to the collection using the [mapper].
     * [me.schlaubi.mongoutils.providers.Accessor] should be used instead.
     *
     * @return a [CompletionStage] that completes when the request to the MongoDB instance was done
     */
    @Suppress("UNCHECKED_CAST") // Cast will succeed because this will always be T
    fun insertAsync(mapper: Mapper<T>): CompletionStage<Unit> {
        this.mapper = mapper
        return mapper.insertAsync(this as T)
    }

    /**
     * Inserts an entity to the collection using the [mapper] and the [insertOptions].
     * [me.schlaubi.mongoutils.providers.Accessor] should be used instead.
     *
     * @return a [CompletionStage] that completes when the request to the MongoDB instance was done
     */
    @Suppress("UNCHECKED_CAST") // Cast will succeed because this will always be T!
    fun insertAsync(mapper: Mapper<T>, insertOptions: InsertOneOptions): CompletionStage<Unit> {
        this.mapper = mapper
        return mapper.insertAsync(this as T, insertOptions)
    }

    /**
     * Inserts an entity to the collection using the [mapper] and the [insertOptions] on the specified [clientSession].
     * [me.schlaubi.mongoutils.providers.Accessor] should be used instead.
     *
     * @return a [CompletionStage] that completes when the request to the MongoDB instance was done
     */
    @Suppress("UNCHECKED_CAST") // Cast will succeed because this will always be T
    fun insertAsync(
        mapper: Mapper<T>,
        insertOptions: InsertOneOptions,
        clientSession: ClientSession
    ): CompletionStage<Unit> {
        this.mapper = mapper
        return mapper.insertAsync(this as T, insertOptions, clientSession)
    }

    /**
     * Inserts an entity to the collection using the [mapper] on the specified [clientSession].
     * [me.schlaubi.mongoutils.providers.Accessor] should be used instead.
     *
     * @return a [CompletionStage] that completes when the request to the MongoDB instance was done
     */
    @Suppress("UNCHECKED_CAST") // Cast will succeed because this will always be T
    fun insertAsync(mapper: Mapper<T>, clientSession: ClientSession): CompletionStage<Unit> {
        this.mapper = mapper
        return mapper.insertAsync(this as T, clientSession)
    }

    /**
     * Inserts an entity to the collection using the [mapper].
     * [me.schlaubi.mongoutils.providers.Accessor] should be used instead.
     *
     * **Warning:** This method blocks the current thread until the process is done
     */
    fun insert(mapper: Mapper<T>): Unit = join(insertAsync(mapper))

    /**
     * Inserts an entity to the collection using the [mapper] and the [insertOptions].
     * [me.schlaubi.mongoutils.providers.Accessor] should be used instead.
     *
     * **Warning:** This method blocks the current thread until the process is done
     */
    fun insert(mapper: Mapper<T>, insertOptions: InsertOneOptions): Unit =
        join(insertAsync(mapper, insertOptions))

    /**
     * Inserts an entity to the collection using the [mapper] on the specified [clientSession].
     * [me.schlaubi.mongoutils.providers.Accessor] should be used instead.
     *
     * **Warning:** This method blocks the current thread until the process is done
     */
    fun insert(mapper: Mapper<T>, clientSession: ClientSession): Unit = join(insertAsync(mapper, clientSession))

    /**
     * Inserts an entity to the collection using the [mapper] and the [insertOptions] on the specified [clientSession].
     * [me.schlaubi.mongoutils.providers.Accessor] should be used instead.
     *
     * **Warning:** This method blocks the current thread until the process is done
     */
    fun insert(mapper: Mapper<T>, insertOptions: InsertOneOptions, clientSession: ClientSession): Unit =
        join(insertAsync(mapper, insertOptions, clientSession))

    /**
     * Updates the entity in the database after it got modified.
     *
     * @return a [CompletionStage] containing the  [UpdateResult]
     */
    @Suppress("UNCHECKED_CAST") // Cast will succeed because this will always be T
    fun updateAsync(): CompletionStage<UpdateResult> {
        checkMapper()
        return mapper.saveAsync(this as T)
    }

    /**
     * Updates the entity in the database after it got modified on the [clientSession].
     *
     * @return a [CompletionStage] containing the [UpdateResult]
     */
    @Suppress("UNCHECKED_CAST") // Cast will succeed because this will always be T
    fun updateAsync(clientSession: ClientSession): CompletionStage<UpdateResult> {
        checkMapper()
        return mapper.saveAsync(this as T, clientSession)
    }

    /**
     * Updates the entity in the database after it got modified.
     *
     * **Warning:** This method blocks the current thread until the process is done
     * @return the [UpdateResult]
     */
    fun update(): UpdateResult = join(updateAsync())

    /**
     * Updates the entity in the database after it got modified on the [clientSession].
     *
     * **Warning:** This method blocks the current thread until the process is done
     * @return the [UpdateResult]
     */
    fun update(clientSession: ClientSession): UpdateResult = join(updateAsync(clientSession))

    /**
     * Deletes the entity from the database.
     *
     * @return a [CompletionStage] containing the [DeleteResult]
     */
    @Suppress("UNCHECKED_CAST") // Cast will succeed because this will always be T
    fun deleteAsync(): CompletionStage<DeleteResult> {
        checkMapper()
        return mapper.deleteAsync(this as T)
    }

    /**
     * Deletes the entity from the database with [deleteOptions].
     *
     * @return a [CompletionStage] containing the [DeleteResult]
     */
    @Suppress("UNCHECKED_CAST") // Cast will succeed because this will always be T
    fun deleteAsync(deleteOptions: DeleteOptions): CompletionStage<DeleteResult> {
        checkMapper()
        return mapper.deleteAsync(this as T, deleteOptions)
    }

    /**
     * Deletes the entity from the database on [clientSession].
     *
     * @return a [CompletionStage] containing the [DeleteResult]
     */
    @Suppress("UNCHECKED_CAST") // Cast will succeed because this will always be T
    fun deleteAsync(clientSession: ClientSession): CompletionStage<DeleteResult> {
        checkMapper()
        return mapper.deleteAsync(this as T, clientSession)
    }

    /**
     * Deletes the entity from the database with [deleteOptions] on [clientSession].

     * @return a [CompletionStage] containing the [DeleteResult]
     */
    @Suppress("UNCHECKED_CAST") // Cast will succeed because this will always be T
    fun deleteAsync(deleteOptions: DeleteOptions, clientSession: ClientSession): CompletionStage<DeleteResult> {
        checkMapper()
        return mapper.deleteAsync(this as T, deleteOptions, clientSession)
    }

    /**
     * Deletes the entity from the database.
     * **Warning:** This method blocks the current thread until the process is done
     *
     * @return the [DeleteResult]
     */
    fun delete(): DeleteResult = join(deleteAsync())

    /**
     * Deletes the entity from the database with [deleteOptions].
     * **Warning:** This method blocks the current thread until the process is done
     *
     * @return the [DeleteResult]
     */
    fun delete(deleteOptions: DeleteOptions): DeleteResult = join(deleteAsync(deleteOptions))

    /**
     * Deletes the entity from the database on [clientSession].
     * **Warning:** This method blocks the current thread until the process is done
     *
     * @return the [DeleteResult]
     */
    fun delete(clientSession: ClientSession): DeleteResult = join(deleteAsync(clientSession))

    /**
     * Deletes the entity from the database with [deleteOptions] on [clientSession].
     * **Warning:** This method blocks the current thread until the process is done
     *
     * @return the [DeleteResult]
     */
    fun delete(deleteOptions: DeleteOptions, clientSession: ClientSession): DeleteResult =
        join(deleteAsync(deleteOptions, clientSession))

    private fun checkMapper() =
        if (!this::mapper.isInitialized) throw IllegalStateException("It looks like there has no mapper been injected yet. Maybe you need to call insertAsync()") else Unit

}