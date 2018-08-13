package io.lamart.kostore.operators

import io.lamart.kostore.Initializer
import io.lamart.kostore.Middleware
import io.lamart.kostore.OptionalInitializer
import io.lamart.kostore.Reducer

class FilterInitializer<T>(
        private val initializer: Initializer<T>,
        private val predicate: T.(T) -> Boolean
) : OptionalInitializer<T> {

    override fun addMiddleware(middleware: Middleware<T?>) =
            initializer.addMiddleware { getState, dispatch, action, next ->
                middleware(
                        { getState().takeIf { predicate(it, it) } },
                        dispatch,
                        action,
                        next
                )
            }

    override fun addReducer(reducer: Reducer<T>) =
            initializer.addReducer { state, action ->
                when {
                    predicate(state, state) -> reducer(state, action)
                    else -> state
                }
            }

}

class FilterOptionalInitializer<T>(
        private val initializer: OptionalInitializer<T>,
        private val predicate: T.(T) -> Boolean
) : OptionalInitializer<T> {

    override fun addMiddleware(middleware: Middleware<T?>) =
            initializer.addMiddleware { getState, dispatch, action, next ->
                middleware(
                        { getState()?.takeIf { predicate(it, it) } },
                        dispatch,
                        action,
                        next
                )
            }

    override fun addReducer(reducer: Reducer<T>) =
            initializer.addReducer { state, action ->
                when {
                    predicate(state, state) -> reducer(state, action)
                    else -> state
                }
            }

}