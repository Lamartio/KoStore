package io.lamart.kosmos.util

object Observer {

    fun <T> wrap(observers: Iterable<(T) -> Unit>): (T) -> Unit {
        var result = { state: T -> }

        observers.forEach { next -> result = result.let { previous -> { previous(it); next(it) } } }

        return result
    }

}