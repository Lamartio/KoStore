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
        accumulated: Middleware<T>,
        middleware: Middleware<T>
): Middleware<T> =
        { getState, dispatch, action, next ->
            accumulated(getState, dispatch, action, { action ->
                middleware(getState, dispatch, action, next)
            })
        }

@Suppress("NAME_SHADOWING")
inline fun <T, reified A : Any> combineFiltered(
        crossinline accumulated: FilteredMiddleware<T, A>,
        crossinline middleware: FilteredMiddleware<T, A>
): FilteredMiddleware<T, A> =
        { getState, dispatch, action, next ->
            accumulated(getState, dispatch, action, { action ->
                filter(middleware).invoke(getState, dispatch, action, next)
            })
        }

fun <T> beforeNext(middleware: Middleware<T>): Middleware<T> =
        { getState, dispatch, action, next ->
            middleware(getState, dispatch, action, next)
            next(action)
        }

inline fun <T, reified A : Any> beforeNextFiltered(crossinline middleware: FilteredMiddleware<T, A>): Middleware<T> =
        { getState, dispatch, action, next ->
            filter(middleware).invoke(getState, dispatch, action, next)
            next(action)
        }

fun <T> afterNext(middleware: Middleware<T>): Middleware<T> =
        { getState, dispatch, action, next ->
            next(action)
            middleware(getState, dispatch, action, next)
        }

inline fun <T, reified A : Any> afterNextFiltered(crossinline middleware: FilteredMiddleware<T, A>): Middleware<T> =
        { getState, dispatch, action, next ->
            next(action)
            filter(middleware).invoke(getState, dispatch, action, next)
        }