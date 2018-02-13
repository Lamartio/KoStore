package io.lamart.kosmos

open class CompositeMiddleware<T> : (Store<T>, Any, (Any) -> Unit) -> Unit {

    private var middleware: (Store<T>, Any, (Any) -> Unit) -> Unit = { store, action, next -> next(action) }

    constructor(vararg middlewares: (Store<T>, Any, (Any) -> Unit) -> Unit) {
        middlewares.forEach { add(it) }
    }

    constructor(init: CompositeMiddleware<T>.() -> Unit) {
        init()
    }

    override fun invoke(store: Store<T>, action: Any, next: (Any) -> Unit) = middleware(store, action, next)

    fun add(middleware: (Store<T>, Any, (Any) -> Unit) -> Unit): CompositeMiddleware<T> = apply {
        this.middleware = combineMiddlewares(this.middleware, middleware)
    }

    companion object {

        fun <T> combineMiddlewares(
                previous: (Store<T>, Any, (Any) -> Unit) -> Unit,
                next: (Store<T>, Any, (Any) -> Unit) -> Unit
        ): (Store<T>, Any, (Any) -> Unit) -> Unit =
                { getState, action, next ->
                    previous(getState, action, { action ->
                        next(getState, action, next)
                    })
                }
    }

}