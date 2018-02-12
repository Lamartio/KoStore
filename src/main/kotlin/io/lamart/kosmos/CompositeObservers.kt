package io.lamart.kosmos

open class CompositeObservers<T> : (T) -> Unit {

    private var observers: MutableList<(T) -> Unit> = mutableListOf()
    private var observer: (T) -> Unit = {}

    constructor(vararg observers: (T) -> Unit) {
        this.observers.addAll(observers)
    }

    constructor(init: CompositeObservers<T>.() -> Unit) {
        init()
    }

    override fun invoke(state: T) = observer(state)

    fun add(observer: (T) -> Unit): CompositeObservers<T> = apply {
        this.observer = observers
                .apply { add(observer) }
                .let { it as Iterable<(T) -> Unit> }
                .let(Util::wrapObservers)
    }

    fun remove(observer: (T) -> Unit): CompositeObservers<T> = apply {
        this.observer = observers
                .apply { remove(observer) }
                .let { it as Iterable<(T) -> Unit> }
                .let(Util::wrapObservers)
    }

}