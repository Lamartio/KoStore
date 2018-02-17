package io.lamart.kosmos.util

import io.lamart.kosmos.StoreSource

object Middleware {

    inline fun <T, reified A> typed(crossinline middleware: (StoreSource<T>, A, (Any) -> Unit) -> Unit): (StoreSource<T>, Any, (Any) -> Unit) -> Unit =
            { store, action, next ->
                if (action is A) middleware(store, action, next)
                else next(action)
            }

    fun <T> combine(
            previous: (StoreSource<T>, Any, (Any) -> Unit) -> Unit,
            next: (StoreSource<T>, Any, (Any) -> Unit) -> Unit
    ): (StoreSource<T>, Any, (Any) -> Unit) -> Unit =
            { getState, action, next ->
                previous(getState, action, { action ->
                    next(getState, action, next)
                })
            }
}