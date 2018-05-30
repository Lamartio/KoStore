package io.lamart.kostore.initializers

import io.lamart.kostore.Middleware
import io.lamart.kostore.Reducer

fun <T, R> Initializer<T>.compose(
        map: (T) -> R,
        reduce: T.(R) -> T,
        block: Initializer<R>.() -> Unit
) = compose(map, reduce).run(block)

fun <T, R> Initializer<T>.compose(map: (T) -> R, reduce: T.(R) -> T): Initializer<R> =
        object : Initializer<R> {

            val initializer = this@compose

            override fun addMiddleware(middleware: Middleware<R>) {
                initializer.addMiddleware { getState, dispatch, action, next ->
                    middleware(
                            { getState().let(map) },
                            dispatch,
                            action,
                            next
                    )
                }
            }

            override fun addReducer(reducer: Reducer<R>) =
                    initializer.addReducer { state, action ->
                        map(state)
                                .let { reducer(it, action) }
                                .let { state.reduce(it) }
                    }

        }