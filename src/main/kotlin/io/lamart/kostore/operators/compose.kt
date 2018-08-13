package io.lamart.kostore.operators

import io.lamart.kostore.Initializer
import io.lamart.kostore.Middleware
import io.lamart.kostore.OptionalInitializer
import io.lamart.kostore.Reducer

class ComposeInitializer<T, R>(
        private val initializer: Initializer<T>,
        private val map: (T) -> R,
        private val reduce: T.(R) -> T
) : Initializer<R> {

    override fun addMiddleware(middleware: Middleware<R>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            middleware.invoke({ getState().let(map) }, dispatch, action, next)
        }
    }

    override fun addReducer(reducer: Reducer<R>) {
        initializer.addReducer { state, action ->
            map(state).let { reducer(it, action) }.let { reduce(state, it) }
        }
    }

}

class ComposeOptionalInitializer<T, R>(
        private val initializer: OptionalInitializer<T>,
        private val map: (T) -> R,
        private val reduce: T.(R) -> T
) : OptionalInitializer<R> {

    override fun addMiddleware(middleware: Middleware<R?>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            middleware.invoke({ getState()?.let(map) }, dispatch, action, next)
        }
    }

    override fun addReducer(reducer: Reducer<R>) {
        initializer.addReducer { state, action ->
            map(state).let { reducer(it, action) }.let { reduce(state, it) }
        }
    }

}