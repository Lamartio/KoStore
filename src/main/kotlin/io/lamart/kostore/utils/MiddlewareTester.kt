package io.lamart.kostore.utils

import io.lamart.kostore.Middleware

fun <T> Middleware<T>.test() = MiddlewareTester(null, this)
fun <T> Middleware<T>.test(state: T) = MiddlewareTester(state, this)

class MiddlewareTester<T>(private val state: T?, private val middleware: Middleware<T>) {

    private val actions: MutableList<Any> = mutableListOf()
    val results : List<Any> = mutableListOf()
    val dispatches : List<Any> = mutableListOf()

    fun dispatch(action: Any): MiddlewareTester<T> = apply { actions.add(action) }

    operator fun invoke(): MiddlewareTester<T> = apply {
        actions.forEach {
            middleware(
                    { state!! },
                    { dispatches.let { it as MutableList }.add(it) },
                    it,
                    { results.let { it as MutableList }.add(it) })
        }
        actions.clear()
    }


}