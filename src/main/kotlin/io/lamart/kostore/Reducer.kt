package io.lamart.kostore

typealias Reducer<T> = (T, Any) -> T
typealias FilteredReducer<T, A> = (T, A) -> T

fun <T> combine(previous: Reducer<T>, next: Reducer<T>): Reducer<T> =
        { state, action -> previous(state, action).let { next(it, action) } }

inline fun <T, reified A> filter(crossinline reducer: FilteredReducer<T, A>): Reducer<T> =
        { state, action ->
            if (action is A) reducer(state, action)
            else state
        }

