package me.schlaubi.mongoutils.annotations

/**
 * Annotation that marks the constructor used by [me.schlaubi.mongoutils.cache.LoadingCache].
 *
 * @see me.schlaubi.mongoutils.cache.LoadingCache
 * @see me.schlaubi.mongoutils.defaults.SnowflakeMongoEntity
 *
 * @author Michael Rittmeister
 * @since 1.0
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class CacheConstructor