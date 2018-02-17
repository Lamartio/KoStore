package io.lamart.kosmos

import io.lamart.kosmos.util.Observer

open class CompositeObserver<T> : (T) -> Unit {

    private var observers: MutableList<(T) -> Unit> = mutableListOf()
    private var observer: (T) -> Unit = {}

    constructor(vararg observers: (T) -> Unit) {
        this.observers.addAll(observers)
    }

    constructor(init: CompositeObserver<T>.() -> Unit) {
        init()
    }

    override fun invoke(state: T) = observer(state)

    fun add(observer: (T) -> Unit): CompositeObserver<T> = apply {
        this.observer = observers
                .apply { add(observer) }
                .let { it as Iterable<(T) -> Unit> }
                .let(Observer::wrap)
    }

    fun remove(observer: (T) -> Unit): CompositeObserver<T> = apply {
        this.observer = observers
                .apply { remove(observer) }
                .let { it as Iterable<(T) -> Unit> }
                .let(Observer::wrap)
    }


}