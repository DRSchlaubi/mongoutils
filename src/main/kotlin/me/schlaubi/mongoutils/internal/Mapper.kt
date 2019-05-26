package me.schlaubi.mongoutils.internal

import com.mongodb.client.ClientSession
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.DeleteOptions
import com.mongodb.client.model.Filters
import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.model.Updates
import com.mongodb.client.result.UpdateResult
import me.schlaubi.mongoutils.MongoEntity
import org.bson.codecs.pojo.ClassModel
import org.bson.codecs.pojo.PropertyAccessor
import java.io.Closeable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutorService
import java.util.function.Supplier

class Mapper<T : MongoEntity<T>>(
    internal val collection: MongoCollection<T>,
    internal val executor: ExecutorService,
    model: ClassModel<T>
) : Closeable {

    private val idProperty: PropertyAccessor<*> = model.idPropertyModel.propertyAccessor
    private val propertyAssociations: Map<PropertyAccessor<*>, String> =
        model.propertyModels.filter { it.isWritable && it != model.idPropertyModel }
            .associateBy({ it.propertyAccessor }, { it.readName })

    fun saveAsync(instance: T): CompletionStage<UpdateResult> = future {
        collection.updateOne(
            mapFindBson(instance),
            mapUpdateBson(instance)
        )
    }

    fun saveAsync(instance: T, clientSession: ClientSession): CompletionStage<UpdateResult> = future {
        collection.updateOne(
            clientSession,
            mapFindBson(instance),
            mapUpdateBson(instance)
        )
    }


    fun insertAsync(
        instance: T,
        options: InsertOneOptions,
        clientSession: ClientSession
    ) = future {
        checkExistence(instance)
        collection.insertOne(clientSession, instance, options)
    }

    fun insertAsync(instance: T, options: InsertOneOptions) = future {
        checkExistence(instance)
        collection.insertOne(instance, options)
    }

    fun insertAsync(instance: T, clientSession: ClientSession) = future {
        checkExistence(instance)
        collection.insertOne(clientSession, instance)
    }

    fun insertAsync(instance: T) = future {
        checkExistence(instance)
        collection.insertOne(instance)
    }

    fun deleteAsync(
        instance: T,
        options: DeleteOptions,
        clientSession: ClientSession
    ) = future {
        collection.deleteMany(clientSession, mapFindBson(instance), options)
    }

    fun deleteAsync(instance: T, options: DeleteOptions) = future {
        collection.deleteMany(mapFindBson(instance), options)
    }

    fun deleteAsync(instance: T, clientSession: ClientSession) = future {
        collection.deleteMany(clientSession, mapFindBson(instance))
    }

    fun deleteAsync(instance: T) = future {
        collection.deleteOne(mapFindBson(instance))
    }

    private fun checkExistence(instance: T) {
        if (collection.find(Filters.eq(instance.bsonId)).iterator().hasNext()) {
            throw IllegalStateException("There is already an object with that id!")
        }
    }

    private fun mapUpdateBson(instance: T) = Updates.combine(
        propertyAssociations
            .asSequence()
            .map { (field, name) -> Updates.set(name, getParameter(field, instance)) }
            .toList()
    )

    private fun mapFindBson(instance: T) = Filters.eq(instance.bsonId)

    private fun <E> getParameter(field: PropertyAccessor<E>, instance: T) = field.get(instance)

    private fun <E> future(block: () -> E): CompletionStage<E> =
        CompletableFuture.supplyAsync(Supplier(block), executor)

    private val T.bsonId
        get() = getParameter(idProperty, this) ?: throw IllegalStateException("Id cannot be null!")

    override fun close() {
        if (!executor.isShutdown && !executor.isShutdown) {
            executor.shutdownNow()
        }
    }

}

