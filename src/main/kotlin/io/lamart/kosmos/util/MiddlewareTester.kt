package io.lamart.kosmos.util

import io.lamart.kosmos.StoreSource

class MiddlewareTester<T> : ((List<Any>) -> Unit) -> Unit {

    private val middleware: (StoreSource<T>, Any, (Any) -> Unit) -> Unit
    private val actions: MutableList<Any>
    private val store: StoreSource<T>

    constructor(middleware: (StoreSource<T>, Any, (Any) -> Unit) -> Unit) : this(null, middleware)

    constructor(state: T?, middleware: (StoreSource<T>, Any, (Any) -> Unit) -> Unit) {
        this.middleware = middleware
        this.actions = mutableListOf()
        this.store = object : StoreSource<T> {

            override val state: T get() = state!!

            override fun invoke(action: Any) = middleware(this, action, { actions.add(it) })

            override fun dispatch(action: Any): StoreSource<T> = apply { invoke(action) }

        }
    }

    fun dispatch(action: Any): MiddlewareTester<T> = apply { actions.add(action) }

    override fun invoke(block: (List<Any>) -> Unit) {
        val result = mutableListOf<Any>()

        actions.forEach { middleware(store, it, { result.add(it) }) }
        actions.clear()

        result.also(block)
    }

}

fun <T> ((StoreSource<T>, Any, (Any) -> Unit) -> Unit).test() = MiddlewareTester(null, this)
fun <T> ((StoreSource<T>, Any, (Any) -> Unit) -> Unit).test(state: T) = MiddlewareTester(state, this)