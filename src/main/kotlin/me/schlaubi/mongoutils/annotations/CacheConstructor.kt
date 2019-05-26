package me.schlaubi.mongoutils.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class CacheConstructor