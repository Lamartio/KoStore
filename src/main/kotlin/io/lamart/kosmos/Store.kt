package io.lamart.kosmos

open class Store<T>(state: T) : (Any) -> Unit {

    @Volatile
    var state: T = state
        private set

    private var observer = CompositeObserver<T>()
    private var middleware = CompositeMiddleware<T>()
    private var reducer = CompositeReducer<T>()

    constructor(state: T, init: Store<T>.() -> Unit) : this(state) {
        init()
    }

    override fun invoke(action: Any) {
        middleware(this, action, { reducer(state, it).also { state = it }.also(observer) })
    }

    fun dispatch(action: Any): Store<T> = apply { this(action) }

    fun add(init: Store<T>.() -> Unit) : Store<T> = apply {
        init()
    }

    fun addMiddleware(middleware: (Store<T>, Any, (Any) -> Unit) -> Unit): Store<T> =
            apply { this.middleware.add(middleware) }

    fun addReducer(reducer: (T, Any) -> T): Store<T> = apply { this.reducer.add(reducer) }

    fun addObserver(observer: (T) -> Unit): Store<T> = apply { this.observer.add(observer) }

    fun removeObserver(observer: (T) -> Unit): Store<T> = apply { this.observer.remove(observer) }

}