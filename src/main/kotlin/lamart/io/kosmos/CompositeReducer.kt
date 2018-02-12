package lamart.io.kosmos

open class CompositeReducer<T> : (T, Any) -> T {

    private var reducer: (T, Any) -> T = { state, action -> state }

    constructor(vararg reducers: (T, Any) -> T) {
        reducers.forEach { add(it) }
    }

    constructor(init: CompositeReducer<T>.() -> Unit) {
        init()
    }

    override fun invoke(state: T, action: Any): T = reducer(state, action)

    fun add(reducer: (T, Any) -> T): CompositeReducer<T> = apply {
        this.reducer = StoreUtil.combineReducers(this.reducer, reducer)
    }

}