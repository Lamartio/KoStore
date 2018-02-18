package io.lamart.kosmos.util

import io.lamart.kosmos.StoreSource

object Interceptor {

    fun <T> wrap(vararg interceptors: (StoreSource<T>) -> StoreSource<T>): (StoreSource<T>) -> StoreSource<T> =
            wrap(interceptors.asIterable())

    fun <T> wrap(interceptors: Iterable<(StoreSource<T>) -> StoreSource<T>>): (StoreSource<T>) -> StoreSource<T> {
        var result: (StoreSource<T>) -> StoreSource<T> = { it }

        interceptors.forEach { next -> result = result.let { previous -> combine(previous, next) } }

        return result
    }

    fun <T> combine(
            previous: (StoreSource<T>) -> StoreSource<T>,
            next: (StoreSource<T>) -> StoreSource<T>
    ): (StoreSource<T>) -> StoreSource<T> = { it.let(previous).let(next) }

}