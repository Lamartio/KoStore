package io.lamart.kostore

typealias Reducer<T> = FilteredReducer<T, Any>

typealias FilteredReducer<T, A> = (T, A) -> T

inline fun <T, reified A : Any> filter(crossinline reducer: FilteredReducer<T, A>): Reducer<T> =
        { state, action ->
            if (action is A) reducer(state, action)
            else state
        }

fun <T> combine(previous: Reducer<T>, next: Reducer<T>): Reducer<T> =
        { state, action -> previous(state, action).let { next(it, action) } }