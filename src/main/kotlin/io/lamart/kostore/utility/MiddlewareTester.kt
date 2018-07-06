package io.lamart.kostore.utility

import io.lamart.kostore.Middleware

fun <T> Middleware<T>.test() =
        MiddlewareTester(null, this)

fun <T> Middleware<T>.test(state: T) =
        MiddlewareTester(state, this)

class MiddlewareTester<T>(val state: T?, val middleware: Middleware<T>) {

    val nexts: List<Any> = mutableListOf()
    val dispatches: List<Any> = mutableListOf()

    operator fun invoke(vararg actions: Any): MiddlewareTester<T> = invoke(actions.toList())

    operator fun invoke(actions: List<Any>): MiddlewareTester<T> = apply {
        actions.forEach {
            middleware(
                    { state!! },
                    { dispatches.let { it as MutableList }.add(it) },
                    it,
                    { nexts.let { it as MutableList }.add(it) })
        }
    }


}