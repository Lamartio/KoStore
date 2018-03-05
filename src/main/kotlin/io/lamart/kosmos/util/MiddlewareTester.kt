package io.lamart.kosmos.util

import io.lamart.kosmos.Middleware
import io.lamart.kosmos.StoreSource

fun <T> Middleware<T>.test() = MiddlewareTester(null, this)
fun <T> Middleware<T>.test(state: T) = MiddlewareTester(state, this)

class MiddlewareTester<T> : ((List<Any>) -> Unit) -> Unit {

    private val middleware: Middleware<T>
    private val actions: MutableList<Any>
    private val store: StoreSource<T>

    constructor(middleware: Middleware<T>) : this(null, middleware)

    constructor(state: T?, middleware: Middleware<T>) {
        this.middleware = middleware
        this.actions = mutableListOf()
        this.store = object : StoreSource<T> {

            override val state: T get() = state!!

            override fun dispatch(action: Any) = middleware(this, action, { actions.add(it) })

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