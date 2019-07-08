package me.schlaubi.mongoutils.providers

import me.schlaubi.mongoutils.MongoEntity
import me.schlaubi.mongoutils.internal.Mapper
import java.util.concurrent.CompletionStage

class MapperAccessor<T : MongoEntity<T>, K : Any>(private val mapper: Mapper<T>) :
    CollectionAccessor<T, K>(mapper.collection, mapper.executor) {

    override fun getAsync(key: K): CompletionStage<T> = super.getAsync(key).thenApply { it.mapper = mapper; it }

    override fun insertAsync(instance: T) = instance.insertAsync(mapper)

    override fun getAllAsync(): CompletionStage<List<T>> {
        return future {
            val list = mutableListOf<T>()
            collection.find().forEach {
                it.mapper = mapper
                list += it
            }
            return@future list.toList()
        }
    }

}