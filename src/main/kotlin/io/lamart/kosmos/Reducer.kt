package io.lamart.kosmos

import io.lamart.kosmos.util.aggregate

typealias Reducer<T> = (T, Any) -> T

fun <T> combine(vararg reducers: Reducer<T>): Reducer<T> = aggregate(reducers, ::combine) ?: defaultReducer()

fun <T> combine(reducers: Iterable<Reducer<T>>): Reducer<T> = aggregate(reducers, ::combine) ?: defaultReducer()

fun <T> combine(reducers: Iterator<Reducer<T>>): Reducer<T> = aggregate(reducers, ::combine) ?: defaultReducer()

fun <T> combine(previous: Reducer<T>, next: Reducer<T>): Reducer<T> =
        { state, action -> previous(state, action).let { next(it, action) } }

inline fun <T, reified A> typed(crossinline reducer: (T, A) -> T): Reducer<T> =
        { state, action ->
            if (action is A) reducer(state, action)
            else state
        }

private fun <T> defaultReducer(): Reducer<T> = { state, action -> state }
