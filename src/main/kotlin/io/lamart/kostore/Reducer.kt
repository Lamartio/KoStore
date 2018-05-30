package io.lamart.kostore

typealias Reducer<T> = FilteredReducer<T, Any>

typealias FilteredReducer<T, A> = (T, A) -> T

inline fun <T, reified A : Any> filter(crossinline reducer: FilteredReducer<T, A>): Reducer<T> =
        { state, action ->
            if (action is A) reducer(state, action)
            else state
        }

fun <T> combine(combinedReducers: Reducer<T>, reducer: Reducer<T>): Reducer<T> =
        { state, action -> combinedReducers(state, action).let { reducer(it, action) } }

inline fun <T, reified A : Any> combineFiltered(
        crossinline combinedReducers: FilteredReducer<T, A>,
        crossinline reducer: FilteredReducer<T, A>
): FilteredReducer<T, A> =
        { state, action -> combinedReducers(state, action).let { filter(reducer).invoke(it, action) } }
