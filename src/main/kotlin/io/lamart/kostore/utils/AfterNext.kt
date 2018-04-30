package io.lamart.kostore.utils

import io.lamart.kostore.Middleware

class AfterNext<T> internal constructor(private val beforeNext: Middleware<T>) : Middleware<T> {

    override fun invoke(getState: () -> T, dispatch: (Any) -> Unit, action: Any, next: (Any) -> Unit) {
        beforeNext(getState, dispatch, action, next)
        next(action)
    }

    fun after(middleware: Middleware<T>): Middleware<T> =
            { getState, dispatch, action, next ->
                beforeNext(getState, dispatch, action, next)
                next(action)
                middleware(getState, dispatch, action, next)
            }

}