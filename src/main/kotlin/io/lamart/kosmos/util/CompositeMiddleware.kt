package io.lamart.kosmos.util

import io.lamart.kosmos.Middleware
import io.lamart.kosmos.StoreSource

open class CompositeMiddleware<T> : Middleware<T> {

    private var middleware: Middleware<T> = Middleware.from { store, action, next -> next(action) }

    constructor(vararg middlewares: (StoreSource<T>, Any, (Any) -> Unit) -> Unit) {
        middlewares.forEach { add(it) }
    }

    constructor(init: CompositeMiddleware<T>.() -> Unit) {
        init()
    }

    override fun invoke(store: StoreSource<T>, action: Any, next: (Any) -> Unit) = middleware(store, action, next)

    fun add(middleware: (StoreSource<T>, Any, (Any) -> Unit) -> Unit): CompositeMiddleware<T> = apply {
        this.middleware = Middleware.combine(this.middleware, middleware)
    }

}