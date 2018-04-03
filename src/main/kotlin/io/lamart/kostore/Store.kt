package io.lamart.kostore


open class Store<T>(private var state: T) : StoreInitializer<T> {

    private var middleware: Middleware<T> = middleware()
    private var reducer: Reducer<T> = reducer()
    private var observer: Observer<T> = observer()

    constructor(state: T, init: Store<T>.() -> Unit) : this(state) {
        init()
    }

    fun getState(): T = state

    infix fun dispatch(action: Any): Unit = middleware(
            ::getState,
            ::dispatch,
            action,
            { reducer(state, it).also { state = it }.also(observer) }
    )

    infix fun addObserver(observer: Observer<T>) {
        this.observer = combine(this.observer, observer)
    }

    override infix fun addReducer(reducer: Reducer<T>) {
        this.reducer = combine(this.reducer, reducer)
    }

    override infix fun addMiddleware(middleware: Middleware<T>) {
        this.middleware = combine(this.middleware, middleware)
    }

    infix fun wrapMiddleware(wrap: (Middleware<T>) -> Middleware<T>) {
        middleware = wrap(middleware)
    }

}