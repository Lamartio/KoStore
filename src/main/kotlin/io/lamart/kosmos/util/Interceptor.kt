package io.lamart.kosmos.util

import io.lamart.kosmos.StoreSource

object Interceptor {

    fun <T> combine(
            previous: (StoreSource<T>) -> StoreSource<T>,
            next: (StoreSource<T>) -> StoreSource<T>
    ): (StoreSource<T>) -> StoreSource<T> = { it.let(previous).let(next) }

}