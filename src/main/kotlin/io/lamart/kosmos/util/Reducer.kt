package io.lamart.kosmos.util

import io.lamart.kosmos.TableReducer


object Reducer {

    inline fun <T, reified A> typed(crossinline reducer: (T, A) -> T): (T, Any) -> T =
            { state, action ->
                if (action is A) reducer(state, action)
                else state
            }

    fun <T> wrap(vararg reducers: (T, Any) -> T): (T, Any) -> T = wrap(reducers.asIterable())

    fun <T> wrap(reducers: Iterable<(T, Any) -> T>): (T, Any) -> T {
        var result: (T, Any) -> T = { state, action -> state }

        reducers.forEach { next -> result = result.let { previous -> combine(previous, next) } }

        return result
    }

    fun <T> combine(
            previous: (T, Any) -> T,
            next: (T, Any) -> T
    ): (T, Any) -> T = { state, action -> previous(state, action).let { next(it, action) } }

    fun <T> table(init: TableReducer<T>.() -> Unit): TableReducer<T> = TableReducer(init)

}

