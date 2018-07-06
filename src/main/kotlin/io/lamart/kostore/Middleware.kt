package io.lamart.kostore

typealias Middleware<T> = FilteredMiddleware<T, Any>

typealias FilteredMiddleware<T, A> = (
        getState: () -> T,
        dispatch: (Any) -> Unit,
        action: A,
        next: (Any) -> Unit
) -> Unit

inline fun <T, reified A : Any> filter(crossinline middleware: FilteredMiddleware<T, A>): Middleware<T> =
        { getState, dispatch, action, next ->
            if (action is A)
                middleware(getState, dispatch, action, next)
            else
                next(action)
        }

@Suppress("NAME_SHADOWING")
fun <T> combine(
        previous: Middleware<T>,
        next: Middleware<T>
): Middleware<T> =
        { getState, dispatch, action, next ->
            previous(getState, dispatch, action, { action ->
                next(getState, dispatch, action, next)
            })
        }