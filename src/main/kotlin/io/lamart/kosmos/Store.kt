package io.lamart.kosmos

open class Store<T>(@Volatile override var state: T) : StoreSource<T> {

    private var observer = CompositeObserver<T>()
    private var middleware = CompositeMiddleware<T>()
    private var reducer = CompositeReducer<T>()
    private var interceptor = CompositeInterceptor<T>(this)

    constructor(state: T, init: Store<T>.() -> Unit) : this(state) {
        init()
    }

    override fun invoke(): T = state

    override fun invoke(action: Any) {
        middleware(interceptor, action, { reducer(state, it).also { state = it }.also(observer) })
    }

    fun dispatch(action: Any): Store<T> = apply { this(action) }

    fun add(init: Store<T>.() -> Unit): Store<T> = apply { init() }

    fun addInterceptor(router: (StoreSource<T>) -> StoreSource<T>): Store<T> = apply { this.interceptor.add(router) }

    fun addMiddleware(middleware: (StoreSource<T>, Any, (Any) -> Unit) -> Unit): Store<T> =
            apply { this.middleware.add(middleware) }

    fun addReducer(reducer: (T, Any) -> T): Store<T> = apply { this.reducer.add(reducer) }

    fun addObserver(observer: (T) -> Unit): Store<T> = apply { this.observer.add(observer) }

    fun removeObserver(observer: (T) -> Unit): Store<T> = apply { this.observer.remove(observer) }

}