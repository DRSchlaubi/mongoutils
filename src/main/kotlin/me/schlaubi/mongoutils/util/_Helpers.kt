package me.schlaubi.mongoutils.util

import java.util.concurrent.CompletionStage

internal fun <F> join(future: CompletionStage<F>) = future.toCompletableFuture().join()

