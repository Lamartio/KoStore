package io.lamart.kosmos

fun <T> ((T, Any) -> T).toReducer(): Reducer<T> = Reducer.from(this)

interface Reducer<T> : (T, Any) -> T {

    companion object {

        fun <T> from(reducer: (T, Any) -> T): Reducer<T> = object : Reducer<T> {
            override fun invoke(state: T, action: Any): T = reducer(state, action)
        }

        inline fun <T, reified A> typed(crossinline reducer: (T, A) -> T): Reducer<T> =
                from { state, action ->
                    if (action is A) reducer(state, action)
                    else state
                }

        fun <T> wrap(vararg reducers: (T, Any) -> T): Reducer<T> = wrap(reducers.asIterable())

        fun <T> wrap(reducers: Iterable<(T, Any) -> T>): Reducer<T> {
            var result: Reducer<T> = from { state, action -> state }

            reducers.forEach { next -> result = result.let { previous -> combine(previous, next) } }

            return result
        }

        fun <T> combine(
                previous: (T, Any) -> T,
                next: (T, Any) -> T
        ): Reducer<T> = from { state, action -> previous(state, action).let { next(it, action) } }

    }

}