package io.lamart.kosmos

typealias Reducer<T> = (T, Any) -> T

fun <T> combine(previous: Reducer<T>, next: Reducer<T>): Reducer<T> =
        { state, action -> previous(state, action).let { next(it, action) } }

inline fun <T, reified A> filter(crossinline reducer: (T, A) -> T): Reducer<T> =
        { state, action ->
            if (action is A) reducer(state, action)
            else state
        }

fun <I, O> Reducer<O>.compose(get: (I) -> O, create: I.(O) -> I): Reducer<I> = { state: I, action: Any ->
    get(state)
            .let { invoke(it, action) }
            .let { create(state, it) }
}
