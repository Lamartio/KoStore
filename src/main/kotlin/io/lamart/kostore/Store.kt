package io.lamart.kostore

import io.lamart.kostore.utils.ListObserver

open class Store<T>(
        private var state: T,
        init: Store<T>.() -> Unit = {}
) : StoreSource<T>, Initializer<T>, Observable<T> {

    private var middleware: Middleware<T> = { _, _, action, next -> next(action) }
    private var reducer: Reducer<T> = { state, _ -> state }
    private val observers = ListObserver<T>()

    init {
        init()
    }

    override fun getState(): T = state

    override fun dispatch(action: Any): Unit = middleware(
            ::getState,
            ::dispatch,
            action,
            { reducer(state, it).also { state = it }.also(observers) }
    )

    override fun addObserver(observer: Observer<T>) {
        observers.add(observer)
    }

    override fun removeObserver(observer: Observer<T>) {
        observers.remove(observer)
    }

    override fun addReducer(reducer: Reducer<T>) {
        this.reducer = combine(this.reducer, reducer)
    }

    override fun addMiddleware(middleware: Middleware<T>) {
        this.middleware = combine(this.middleware, middleware)
    }

}