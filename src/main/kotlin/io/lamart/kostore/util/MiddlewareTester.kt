package io.lamart.kostore.util

import io.lamart.kostore.Middleware

fun <T> Middleware<T>.test() = MiddlewareTester(null, this)
fun <T> Middleware<T>.test(state: T) = MiddlewareTester(state, this)

class MiddlewareTester<T>(private val state: T?, private val middleware: Middleware<T>) {

    private val actions: MutableList<Any> = mutableListOf()
    val results = mutableListOf<Any>()
    val dispatches = mutableListOf<Any>()

    fun dispatch(action: Any): MiddlewareTester<T> = apply { actions.add(action) }

    operator fun invoke(): MiddlewareTester<T> = apply {
        actions.forEach {
            middleware(
                    { state!! },
                    { dispatches.add(it) },
                    it,
                    { results.add(it) })
        }
        actions.clear()
    }


}