package io.lamart.kosmos

import io.lamart.kosmos.util.Middleware

open class CompositeMiddleware<T> : (StoreSource<T>, Any, (Any) -> Unit) -> Unit {

    private var middleware: (StoreSource<T>, Any, (Any) -> Unit) -> Unit = { store, action, next -> next(action) }

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