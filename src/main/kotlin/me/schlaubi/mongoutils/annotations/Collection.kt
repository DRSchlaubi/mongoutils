package me.schlaubi.mongoutils.annotations

/**
 * Annotation to represent the [collection] and the [database] of a [me.schlaubi.mongoutils.MongoEntity].
 * **Important:** This annotation has to be placed on every [me.schlaubi.mongoutils.MongoEntity]
 *
 * @see me.schlaubi.mongoutils.MongoEntity
 * @see me.schlaubi.mongoutils.internal.Mapper
 *
 * @author Michael Rittmeister
 * @since 1.0
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Collection(
    /**
     * The name of the collection the entities of this type are saved in.
     */
    val collection: String,
    /**
     * The name of the database the [collection] is in.
     */
    val database: String
)
