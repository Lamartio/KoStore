package io.lamart.kosmos

import io.lamart.kosmos.util.CompositeInterceptor
import io.lamart.kosmos.util.CompositeMiddleware
import io.lamart.kosmos.util.CompositeObserver
import io.lamart.kosmos.util.CompositeReducer

open class Store<T>(@Volatile override var state: T) : StoreSource<T> {

    private val observer = CompositeObserver<T>()
    private val middleware = CompositeMiddleware<T>()
    private val reducer = CompositeReducer<T>()
    private val interceptor = CompositeInterceptor<T>()

    constructor(state: T, init: Store<T>.() -> Unit) : this(state) {
        init()
    }

    override fun invoke(): T = state

    override fun invoke(action: Any) =
            middleware(
                    interceptor(this),
                    action,
                    { reducer(state, it).also { state = it }.also(observer) }
            )

    fun dispatch(action: Any): Store<T> = apply { this(action) }

    fun add(init: Store<T>.() -> Unit): Store<T> = apply { init() }

    fun addInterceptor(router: (StoreSource<T>) -> StoreSource<T>): Store<T> = apply { this.interceptor.add(router) }

    fun addMiddleware(middleware: (StoreSource<T>, Any, (Any) -> Unit) -> Unit): Store<T> =
            apply { this.middleware.add(middleware) }

    fun addReducer(reducer: (T, Any) -> T): Store<T> = apply { this.reducer.add(reducer) }

    fun addObserver(observer: (T) -> Unit): Store<T> = apply { this.observer.add(observer) }

    fun removeObserver(observer: (T) -> Unit): Store<T> = apply { this.observer.remove(observer) }

}