package io.lamart.kosmos

open class Store<T>(state: T) : (Any) -> Unit {

    @Volatile
    var state: T = state
        private set

    private var observers = CompositeObservers<T>()
    private var middleware: (Store<T>, Any, (Any) -> Unit) -> Unit = { _, action, next -> next(action) }
    private var reducer: (T, Any) -> T = { state, action -> state }

    constructor(state: T, init: Store<T>.() -> Unit) : this(state) {
        init()
    }

    override fun invoke(action: Any) {
        middleware(this, action, { reducer(state, it).also { state = it }.also(observers) })
    }

    fun dispatch(action: Any): Store<T> = apply { this(action) }

    fun addMiddleware(middleware: (Store<T>, Any, (Any) -> Unit) -> Unit): Store<T> = apply {
        this.middleware = Util.combineMiddlewares(this.middleware, middleware)
    }

    fun addReducer(reducer: (T, Any) -> T): Store<T> = apply {
        this.reducer = Util.combineReducers(this.reducer, reducer)
    }

    fun addObserver(observer: (T) -> Unit): Store<T> = apply { observers.add(observer) }

    fun removeObserver(observer: (T) -> Unit): Store<T> = apply { observers.remove(observer) }

}