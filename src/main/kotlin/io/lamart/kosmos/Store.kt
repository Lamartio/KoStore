package io.lamart.kosmos


open class Store<T>(@Volatile override var state: T) : StoreSource<T> {

    var middleware: Middleware<T> = { _, action, next -> next(action) }
        private set
    var reducer: Reducer<T> = { state, _ -> state }
        private set
    var observer: Observer<T> = { }
        private set

    constructor(state: T, init: Store<T>.() -> Unit) : this(state) {
        init()
    }

    override fun dispatch(action: Any) =
            middleware(
                    this,
                    action,
                    { reducer(state, it).also { state = it }.also(observer) }
            )

    fun add(init: Store<T>.() -> Unit): Store<T> = apply { init() }

    fun addReducer(reducer: Reducer<T>): Store<T> = apply { this.reducer = combine(this.reducer, reducer) }

    fun wrapReducer(wrap: (Reducer<T>) -> Reducer<T>): Store<T> = apply { reducer = wrap(reducer) }

    operator fun Reducer<T>.plusAssign(reducer: Reducer<T>) {
        addReducer(reducer)
    }

    operator fun Reducer<T>.plusAssign(wrap: (Reducer<T>) -> Reducer<T>) {
        wrapReducer(wrap)
    }

    fun addMiddleware(middleware: Middleware<T>): Store<T> = apply {
        this.middleware = combine(this.middleware, middleware)
    }

    fun wrapMiddleware(wrap: (Middleware<T>) -> Middleware<T>): Store<T> = apply { middleware = wrap(middleware) }

    operator fun Middleware<T>.plusAssign(middleware: Middleware<T>) {
        addMiddleware(middleware)
    }

    operator fun Middleware<T>.plusAssign(wrap: (Middleware<T>) -> Middleware<T>) {
        wrapMiddleware(wrap)
    }

    fun addObserver(observer: Observer<T>): Store<T> = apply { this.observer = combine(this.observer, observer) }

    fun wrapObserver(wrap: (Observer<T>) -> Observer<T>): Store<T> = apply { observer = wrap(observer) }

    @JvmName("addObserverPlusAssign")
    operator fun Observer<T>.plusAssign(observer: Observer<T>) {
        addObserver(observer)
    }

    @JvmName("wrapObserverPlusAssign")
    operator fun Observer<T>.plusAssign(wrap: (Observer<T>) -> Observer<T>) {
        wrapObserver(wrap)
    }

}