package io.lamart.kostore

typealias Middleware<T> = (
        getState: () -> T,
        dispatch: (Any) -> Unit,
        action: Any,
        next: (Any) -> Unit
) -> Unit

typealias FilteredMiddleware<T, A> = (
        getState: () -> T,
        dispatch: (Any) -> Unit,
        action: A,
        next: (Any) -> Unit
) -> Unit

inline fun <T, reified A> filter(crossinline middleware: FilteredMiddleware<T, A>): Middleware<T> =
        { getState, dispatch, action, next ->
            if (action is A)
                middleware(getState, dispatch, action, next)
            else
                next(action)
        }

fun <T> combine(
        previous: Middleware<T>,
        next: Middleware<T>
): Middleware<T> =
        { getState, dispatch, action, next ->
            previous(getState, dispatch, action, { action ->
                next(getState, dispatch, action, next)
            })
        }

fun <T> beforeNext(middleware: Middleware<T>): Middleware<T> =
        { getState, dispatch, action, next ->
            middleware(getState, dispatch, action, next)
            next(action)
        }

fun <T> afterNext(middleware: Middleware<T>): Middleware<T> =
        { getState, dispatch, action, next ->
            next(action)
            middleware(getState, dispatch, action, next)
        }