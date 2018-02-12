package lamart.io.kosmos

import kotlin.properties.Delegates

open class Store<T>(state: T) : (Any) -> Unit {

    var state: T by Delegates.observable(state) { _, _, after -> onStateChanged(after) }
        private set

    private var onStateChanged: (T) -> Unit = {}
    private var middleware: (Store<T>, Any, (Any) -> Unit) -> Unit = { _, action, next -> next(action) }
    private var reducer: (T, Any) -> T = { state, action -> state }

    constructor(state: T, init: Store<T>.() -> Unit) : this(state) {
        init()
    }

    override fun invoke(action: Any) {
        middleware(this, action, { state = reducer(state, it) })
    }

    fun dispatch(action: Any): Store<T> = apply { this(action) }

    fun addMiddleware(middleware: (Store<T>, Any, (Any) -> Unit) -> Unit): Store<T> = apply {
        this.middleware = StoreUtil.combineMiddlewares(this.middleware, middleware)
    }

    fun addReducer(reducer: (T, Any) -> T): Store<T> = apply {
        this.reducer = StoreUtil.combineReducers(this.reducer, reducer)
    }

    fun addListener(onStateChanged: (T) -> Unit): Store<T> = apply {
        this.onStateChanged = StoreUtil.combineListeners(this.onStateChanged, onStateChanged)
    }

}