package io.lamart.kosmos.util

object Observer {

    fun <T> wrap(vararg observers: (T) -> Unit): (T) -> Unit = wrap(observers.asIterable())

    fun <T> wrap(observers: Iterable<(T) -> Unit>): (T) -> Unit {
        var result = { state: T -> }

        observers.forEach { next -> result = result.let { previous -> combine(previous, next) } }

        return result
    }

    fun <T> combine(
            previous: (T) -> Unit,
            next: (T) -> Unit
    ): (T) -> Unit = { previous(it); next(it) }

}