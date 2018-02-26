package io.lamart.kosmos.util

import io.lamart.kosmos.Observer

open class CompositeObserver<T> : Observer<T> {

    private val observers: MutableList<(T) -> Unit> = mutableListOf()
    private var observer: Observer<T> = Observer.from {}

    constructor(vararg observers: (T) -> Unit) {
        observers.forEach { add(it) }
    }

    constructor(init: CompositeObserver<T>.() -> Unit) {
        init()
    }

    override fun invoke(state: T) = observer(state)

    fun add(observer: (T) -> Unit): CompositeObserver<T> = apply {
        this.observer = observers
                .apply { add(observer) }
                .let { Observer.wrap(it) }
    }

    fun remove(observer: (T) -> Unit): CompositeObserver<T> = apply {
        this.observer = observers
                .apply { remove(observer) }
                .let { Observer.wrap(it) }
    }


}