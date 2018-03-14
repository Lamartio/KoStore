package io.lamart.kostore.util

import io.lamart.kostore.Middleware

class MiddlewareAfter<T> internal constructor(private val before: Middleware<T>) : Middleware<T> {

    override fun invoke(getState: () -> T, dispatch: (Any) -> Unit, action: Any, next: (Any) -> Unit) {
        before(getState, dispatch, action, next)
        next(action)
    }

    fun after(middleware: Middleware<T>): Middleware<T> =
            { getState, dispatch, action, next ->
                before(getState, dispatch, action, next)
                next(action)
                middleware(getState, dispatch, action, next)
            }

}