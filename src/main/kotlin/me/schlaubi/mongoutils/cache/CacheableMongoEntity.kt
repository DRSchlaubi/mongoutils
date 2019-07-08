package me.schlaubi.mongoutils.cache

import com.mongodb.client.ClientSession
import com.mongodb.client.model.DeleteOptions
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import me.schlaubi.mongoutils.MongoEntity
import me.schlaubi.mongoutils.internal.Mapper
import java.lang.UnsupportedOperationException
import java.util.concurrent.CompletionStage

abstract class CacheableMongoEntity<T : CacheableMongoEntity<T>> : MongoEntity<T>() {

    internal lateinit var cache: Cache<*, T>

    override fun insertAsync(mapper: Mapper<T>): CompletionStage<Unit> = unsupported()

    override fun insertAsync(mapper: Mapper<T>, insertOptions: InsertOneOptions): CompletionStage<Unit> = unsupported()

    override fun insertAsync(
        mapper: Mapper<T>,
        insertOptions: InsertOneOptions,
        clientSession: ClientSession
    ): CompletionStage<Unit> = unsupported()

    override fun insertAsync(mapper: Mapper<T>, clientSession: ClientSession): CompletionStage<Unit> = unsupported()

    @Suppress("UNCHECKED_CAST")
    override fun updateAsync(): CompletionStage<UpdateResult> {
        cache.update(this as T)
        return super.updateAsync()
    }

    @Suppress("UNCHECKED_CAST")
    override fun updateAsync(clientSession: ClientSession): CompletionStage<UpdateResult> {
        cache.update(this as T)
        return super.updateAsync(clientSession)
    }

    @Suppress("UNCHECKED_CAST")
    override fun deleteAsync(deleteOptions: DeleteOptions): CompletionStage<DeleteResult> {
        cache.invalidate(this as T)
        return super.deleteAsync(deleteOptions)
    }

    @Suppress("UNCHECKED_CAST")
    override fun deleteAsync(clientSession: ClientSession): CompletionStage<DeleteResult> {
        cache.invalidate(this as T)
        return super.deleteAsync(clientSession)
    }

    @Suppress("UNCHECKED_CAST")
    override fun deleteAsync(
        deleteOptions: DeleteOptions,
        clientSession: ClientSession
    ): CompletionStage<DeleteResult> {
        cache.invalidate(this as T)
        return super.deleteAsync(deleteOptions, clientSession)
    }

    private fun unsupported(): Nothing =
        throw UnsupportedOperationException("Cachable mongo entites does not need to get inserted")
}
