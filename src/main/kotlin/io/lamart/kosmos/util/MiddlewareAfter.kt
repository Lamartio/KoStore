package io.lamart.kosmos.util

import io.lamart.kosmos.Middleware
import io.lamart.kosmos.StoreSource

class MiddlewareAfter<T> internal constructor(private val before: (StoreSource<T>, Any, (Any) -> Unit) -> Unit) : Middleware<T> {

    override fun invoke(store: StoreSource<T>, action: Any, next: (Any) -> Unit) = before(store, action, next)

    fun after(middleware: (StoreSource<T>, Any, (Any) -> Unit) -> Unit): Middleware<T> =
            Middleware.from { store, action, next ->
                before(store, action, next)
                next(action)
                middleware(store, action, next)
            }

    fun after(block: (StoreSource<T>) -> Unit): Middleware<T> = after { store, action, next -> block(store) }

}