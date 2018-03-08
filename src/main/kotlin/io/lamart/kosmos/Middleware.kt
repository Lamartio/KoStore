package io.lamart.kosmos

import io.lamart.kosmos.util.MiddlewareAfter

typealias Middleware<T> = (
        getState: () -> T,
        dispatch: (Any) -> Unit,
        action: Any,
        next: (Any) -> Unit
) -> Unit

fun <T> middleware(): Middleware<T> = { _, _, action, next -> next(action) }

fun <T> combine(
        previous: Middleware<T>,
        next: Middleware<T>
): Middleware<T> =
        { getState, dispatch, action, next ->
            previous(getState, dispatch, action, { action ->
                next(getState, dispatch, action, next)
            })
        }

fun <T> before(middleware: Middleware<T>): MiddlewareAfter<T> = MiddlewareAfter(middleware)

fun <T> after(middleware: Middleware<T>): Middleware<T> =
        { getState, dispatch, action, next ->
            next(action)
            middleware(getState, dispatch, action, next)
        }

fun <T, R> Middleware<T>.compose(get: (R) -> T): Middleware<R> = { getState, dispatch, action, next ->
    invoke(
            { getState().let(get) },
            dispatch,
            action,
            next
    )
}