package io.lamart.kosmos.util

import io.lamart.kosmos.StoreSource

object Middleware {

    inline fun <T, reified A> typed(crossinline middleware: (StoreSource<T>, A, (Any) -> Unit) -> Unit): (StoreSource<T>, Any, (Any) -> Unit) -> Unit =
            { store, action, next ->
                if (action is A) middleware(store, action, next)
                else next(action)
            }

    fun <T> wrap(vararg middlewares: (StoreSource<T>, Any, (Any) -> Unit) -> Unit): (StoreSource<T>, Any, (Any) -> Unit) -> Unit =
            wrap(middlewares.asIterable())

    fun <T> wrap(middlewares: Iterable<(StoreSource<T>, Any, (Any) -> Unit) -> Unit>): (StoreSource<T>, Any, (Any) -> Unit) -> Unit {
        var result: (StoreSource<T>, Any, (Any) -> Unit) -> Unit = { store, action, next -> next(action) }

        middlewares.forEach { next -> result = result.let { previous -> combine(previous, next) } }

        return result
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

    fun <T> before(middleware: (StoreSource<T>) -> Unit): After<T> = After { store, _, _ -> middleware(store) }

    fun <T> before(middleware: (StoreSource<T>, Any, (Any) -> Unit) -> Unit): After<T> = After(middleware)
}

class After<T> internal constructor(private val before: (StoreSource<T>, Any, (Any) -> Unit) -> Unit) : (StoreSource<T>, Any, (Any) -> Unit) -> Unit {

    override fun invoke(store: StoreSource<T>, action: Any, next: (Any) -> Unit) = before(store, action, next)

    fun after(middleware: (StoreSource<T>, Any, (Any) -> Unit) -> Unit): (StoreSource<T>, Any, (Any) -> Unit) -> Unit =
            { store, action, next ->
                before(store, action, next)
                next(action)
                middleware(store, action, next)
            }

    fun after(middleware: (StoreSource<T>) -> Unit): (StoreSource<T>, Any, (Any) -> Unit) -> Unit =
            after { store, _, _ -> middleware(store) }

}