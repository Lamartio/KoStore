package lamart.io.kosmos

open class CompositeListener<T> : (T) -> Unit {

    private var listener: (T) -> Unit = {}

    constructor(vararg listeners: (T) -> Unit) {
        listeners.forEach { add(it) }
    }

    constructor(init: CompositeListener<T>.() -> Unit) {
        init()
    }

    override fun invoke(state: T) = listener(state)

    fun add(listener: (T) -> Unit): CompositeListener<T> = apply {
        this.listener = StoreUtil.combineListeners<T>(this.listener, listener)
    }

}