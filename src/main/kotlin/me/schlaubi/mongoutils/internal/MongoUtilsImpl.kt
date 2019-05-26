package me.schlaubi.mongoutils.internal

import com.mongodb.client.MongoClient
import me.schlaubi.mongoutils.MongoEntity
import me.schlaubi.mongoutils.MongoUtils
import me.schlaubi.mongoutils.annotations.Collection
import me.schlaubi.mongoutils.providers.MapperAccessor
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.ClassModel
import org.bson.codecs.pojo.Convention
import org.bson.codecs.pojo.PojoCodecProvider
import java.io.Closeable
import java.util.concurrent.ExecutorService
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

internal class MongoUtilsImpl(
    override val client: MongoClient,
    private val codecRegistry: CodecRegistry,
    private val pojoConventions: List<Convention>?,
    private val executorService: ExecutorService
) : MongoUtils {

    private val models = mutableMapOf<Class<*>, ClassModel<*>>()
    private val mappers = mutableMapOf<Class<*>, Mapper<*>>()

    override fun <T : MongoEntity<T>, K> createAccessor(
        clazz: KClass<T>
    ) = MapperAccessor<T, K>(buildMapper(clazz))

    override fun <T : MongoEntity<T>, K> createAccessor(
        clazz: KClass<T>,
        codecRegistry: CodecRegistry
    ) = MapperAccessor<T, K>(buildMapper(clazz, codecRegistry))

    override fun <T : MongoEntity<T>, K> createAccessor(
        clazz: KClass<T>,
        codecRegistry: CodecRegistry,
        pojoConventions: List<Convention>?
    ) = MapperAccessor<T, K>(buildMapper(clazz, codecRegistry, pojoConventions))

    override fun <T : MongoEntity<T>, K> createAccessor(
        clazz: KClass<T>,
        codecRegistry: CodecRegistry,
        pojoConventions: List<Convention>?,
        executorService: ExecutorService
    ) = MapperAccessor<T, K>(buildMapper(clazz, codecRegistry, pojoConventions, executorService))

    private fun <T : MongoEntity<T>> buildMapper(clazz: KClass<T>) = buildMapper(clazz, codecRegistry)

    private fun <T : MongoEntity<T>> buildMapper(clazz: KClass<T>, codecRegistry: CodecRegistry) =
        buildMapper(clazz, codecRegistry, pojoConventions)

    private fun <T : MongoEntity<T>> buildMapper(
        clazz: KClass<T>,
        codecRegistry: CodecRegistry,
        pojoConventions: List<Convention>?
    ) = buildMapper(clazz, codecRegistry, pojoConventions, executorService)

    @Suppress("UNCHECKED_CAST")
    private fun <T : MongoEntity<T>> buildMapper(
        clazz: KClass<T>,
        codecRegistry: CodecRegistry,
        pojoConventions: List<Convention>?,
        executorService: ExecutorService
    ): Mapper<T> {
        return mappers.computeIfAbsent(clazz.java) {
            val tableInformation =
                clazz.findAnnotation<Collection>()
                    ?: throw IllegalArgumentException("Could not find @Collection annotation!")
            val javaClazz = clazz.java

            val model = getModel(javaClazz, pojoConventions)

            val collection = client
                .getDatabase(tableInformation.database)
                .getCollection(tableInformation.collection, javaClazz)
                .withCodecRegistry(
                    CodecRegistries.fromRegistries(
                        codecRegistry, CodecRegistries.fromProviders(
                            PojoCodecProvider
                                .builder()
                                .register(model)
                                .build()
                        )
                    )
                )
            Mapper(collection, executorService, model)
        } as Mapper<T>
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getModel(clazz: Class<T>, conventions: List<Convention>?): ClassModel<T> =
            models.computeIfAbsent(clazz) {
                val builder = ClassModel.builder(clazz)
                if (conventions != null) {
                    builder.conventions(conventions)
                }
                builder.build()
            } as ClassModel<T>

    override fun close() {
        mappers.values.distinct().forEach(Closeable::close)
    }

}