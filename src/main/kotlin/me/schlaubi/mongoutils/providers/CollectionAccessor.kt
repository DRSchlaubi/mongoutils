package me.schlaubi.mongoutils.providers

import com.mongodb.client.ClientSession
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.DeleteOptions
import com.mongodb.client.model.Filters
import com.mongodb.client.model.InsertManyOptions
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.result.DeleteResult
import me.schlaubi.mongoutils.MongoEntity
import org.bson.conversions.Bson
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutorService
import java.util.function.Supplier

open class CollectionAccessor<T : MongoEntity<T>, K>(
    override val collection: MongoCollection<T>,
    protected val executor: ExecutorService
) : Accessor<T, K> {

    override fun getAsync(key: K): CompletionStage<T> = future { collection.find(Filters.eq(key)).first() }

    override fun getAllAsync(): CompletionStage<List<T>> = future {
        collection.find().toList()
    }

    override fun insertAsync(
        instance: T,
        options: InsertOneOptions,
        clientSession: ClientSession
    ) = future {
        collection.insertOne(clientSession, instance, options)
    }

    override fun insertAsync(instance: T, options: InsertOneOptions) = future {
        collection.insertOne(instance, options)
    }

    override fun insertAsync(instance: T, clientSession: ClientSession) = future {
        collection.insertOne(clientSession, instance)
    }

    override fun insertAsync(instance: T) = future {
        collection.insertOne(instance)
    }

    override fun deleteAsync(
        filter: Bson,
        options: DeleteOptions,
        clientSession: ClientSession
    ) = future {
        collection.deleteMany(clientSession, filter, options)
    }

    override fun deleteAsync(filter: Bson, options: DeleteOptions) = future {
        collection.deleteMany(filter, options)
    }

    override fun deleteAsync(filter: Bson, clientSession: ClientSession) = future {
        collection.deleteMany(clientSession, filter)
    }

    override fun deleteAsync(filter: Bson) = future {
        collection.deleteMany(filter)
    }

    override fun contains(key: K): Boolean {
        return collection.find(Filters.eq(key)).first() != null
    }

    protected fun <E> future(block: () -> E): CompletionStage<E> =
        CompletableFuture.supplyAsync(Supplier(block), executor)
}
