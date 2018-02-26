package io.lamart.kosmos

import io.lamart.kosmos.util.MiddlewareAfter

fun <T> ((StoreSource<T>, Any, (Any) -> Unit) -> Unit).toMiddleware(): Middleware<T> = Middleware.from(this)

interface Middleware<in T> : (StoreSource<T>, Any, (Any) -> Unit) -> Unit {

    companion object {

        fun <T> from(middleware: (StoreSource<T>, Any, (Any) -> Unit) -> Unit): Middleware<T> =
                object : Middleware<T> {
                    override fun invoke(store: StoreSource<T>, action: Any, next: (Any) -> Unit) =
                            middleware(store, action, next)
                }

        inline fun <T, reified A> typed(noinline middleware: (StoreSource<T>, A, (Any) -> Unit) -> Unit): Middleware<T> =
                from { store, action, next ->
                    if (action is A) middleware(store, action, next)
                    else next(action)
                }

        fun <T> wrap(vararg middlewares: (StoreSource<T>, Any, (Any) -> Unit) -> Unit): Middleware<T> =
                wrap(middlewares.asIterable())

        fun <T> wrap(middlewares: Iterable<(StoreSource<T>, Any, (Any) -> Unit) -> Unit>): Middleware<T> {
            var result: Middleware<T> = from { store, action, next -> next(action) }

            middlewares.forEach { next -> result = result.let { previous -> combine(previous, next) } }

            return result
        }

        fun <T> combine(
                previous: (StoreSource<T>, Any, (Any) -> Unit) -> Unit,
                next: (StoreSource<T>, Any, (Any) -> Unit) -> Unit
        ): Middleware<T> =
                from { store, action, next ->
                    previous(store, action, { action ->
                        next(store, action, next)
                    })
                }

        fun <T> before(block: (StoreSource<T>) -> Unit): MiddlewareAfter<T> = MiddlewareAfter { store, _, _ -> block(store) }

        fun <T> before(middleware: (StoreSource<T>, Any, (Any) -> Unit) -> Unit): MiddlewareAfter<T> = MiddlewareAfter(middleware)

        fun <T> after(middleware: (StoreSource<T>, Any, (Any) -> Unit) -> Unit): Middleware<T> =
                Middleware.from { store, action, next ->
                    next(action)
                    middleware(store, action, next)
                }

        fun <T> after(block: (StoreSource<T>) -> Unit): Middleware<T> = after { store, action, next -> block(store) }

    }

}