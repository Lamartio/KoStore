package io.lamart.kosmos.util


object Reducer {

    inline fun <T, reified A> typed(crossinline reducer: (T, A) -> T): (T, Any) -> T =
            { state, action ->
                if (action is A) reducer(state, action)
                else state
            }

    fun <T> combine(previous: (T, Any) -> T, next: (T, Any) -> T): (T, Any) -> T =
            { state, action -> previous(state, action).let { next(it, action) } }

}

