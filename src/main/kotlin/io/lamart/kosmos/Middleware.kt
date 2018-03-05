package io.lamart.kosmos

import io.lamart.kosmos.util.aggregate

typealias Middleware<T> = (StoreSource<T>, Any, (Any) -> Unit) -> Unit

fun <T> combine(vararg items: Middleware<T>): Middleware<T> = aggregate(items, ::combine) ?: defaultMiddleware()

fun <T> combine(items: Iterable<Middleware<T>>): Middleware<T> = aggregate(items, ::combine) ?: defaultMiddleware()

fun <T> combine(items: Iterator<Middleware<T>>): Middleware<T> = aggregate(items, ::combine) ?: defaultMiddleware()

fun <T> combine(
        previous: Middleware<T>,
        next: Middleware<T>
): Middleware<T> =
        { store, action, next ->
            previous(store, action, { action ->
                next(store, action, next)
            })
        }

inline fun <T, reified A> typed(noinline middleware: (StoreSource<T>, A, (Any) -> Unit) -> Unit): Middleware<T> =
        { store, action, next ->
            if (action is A) middleware(store, action, next)
            else next(action)
        }

fun <T> before(middleware: Middleware<T>): After<T> = After(middleware)

fun <T> after(middleware: Middleware<T>): Middleware<T> =
        { store, action, next ->
            next(action)
            middleware(store, action, next)
        }

class After<T> internal constructor(private val before: Middleware<T>) : Middleware<T> {

    override fun invoke(store: StoreSource<T>, action: Any, next: (Any) -> Unit) {
        before(store, action, next)
        next(action)
    }

    fun after(middleware: Middleware<T>): Middleware<T> =
            { store, action, next ->
                before(store, action, next)
                next(action)
                middleware(store, action, next)
            }

}

private fun <T> defaultMiddleware(): Middleware<T> = { store, action, next -> next(action) }
