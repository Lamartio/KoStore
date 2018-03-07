package io.lamart.kosmos


open class Store<T>(@Volatile private var state: T) : StoreInitializer<T> {

    private var middleware: Middleware<T> = { _, _, action, next -> next(action) }
    private var reducer: Reducer<T> = { state, _ -> state }
    private var observer: Observer<T> = { }

    constructor(state: T, init: Store<T>.() -> Unit) : this(state) {
        init()
    }

    fun getState(): T = state

    fun dispatch(action: Any): Unit = middleware(
            ::getState,
            ::dispatch,
            action,
            { reducer(state, it).also { state = it }.also(observer) }
    )

    override fun addReducer(reducer: Reducer<T>) {
        this.reducer = combine(this.reducer, reducer)
    }

    override fun addMiddleware(middleware: Middleware<T>) {
        this.middleware = combine(this.middleware, middleware)
    }

    fun wrapMiddleware(wrap: (Middleware<T>) -> Middleware<T>) {
        middleware = wrap(middleware)
    }

    fun addObserver(observer: Observer<T>) {
        this.observer = combine(this.observer, observer)
    }

}