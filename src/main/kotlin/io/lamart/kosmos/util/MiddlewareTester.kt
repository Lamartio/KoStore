package io.lamart.kosmos.util

import io.lamart.kosmos.Middleware

fun <T> Middleware<T>.test() = MiddlewareTester(null, this)
fun <T> Middleware<T>.test(state: T) = MiddlewareTester(state, this)

class MiddlewareTester<T>(private val state: T?, private val middleware: Middleware<T>) : ((List<Any>) -> Unit) -> Unit {

    private val actions: MutableList<Any> = mutableListOf()

    fun dispatch(action: Any): MiddlewareTester<T> = apply { actions.add(action) }

    override fun invoke(block: (List<Any>) -> Unit) {
        val result = mutableListOf<Any>()

        actions.forEach { middleware({ state!! }, {}, it, { result.add(it) }) }
        actions.clear()

        result.also(block)
    }

}