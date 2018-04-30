package io.lamart.kostore

import io.lamart.kostore.utils.AfterNext

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

fun <T> beforeNext(middleware: Middleware<T>): AfterNext<T> = AfterNext(middleware)

fun <T> afterNext(middleware: Middleware<T>): Middleware<T> =
        { getState, dispatch, action, next ->
            next(action)
            middleware(getState, dispatch, action, next)
        }

fun <T, R> Middleware<T>.compose(map: (R) -> T): Middleware<R> =
        { getState, dispatch, action, next ->
            invoke(
                    { getState().let(map) },
                    dispatch,
                    action,
                    next
            )
        }