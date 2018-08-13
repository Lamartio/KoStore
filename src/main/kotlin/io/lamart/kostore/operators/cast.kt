package io.lamart.kostore.operators

import io.lamart.kostore.Initializer
import io.lamart.kostore.Middleware
import io.lamart.kostore.OptionalInitializer
import io.lamart.kostore.Reducer

@Suppress("UNCHECKED_CAST")
class CastInitializer<T, R>(private val initializer: Initializer<T>) : Initializer<R> {

    override fun addMiddleware(middleware: Middleware<R>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            middleware({ getState() as R }, dispatch, action, next)
        }
    }

    override fun addReducer(reducer: Reducer<R>) {
        initializer.addReducer { state, action ->
            reducer(state as R, action) as T
        }
    }
}

@Suppress("UNCHECKED_CAST")
class CastOptionalInitializer<T, R>(private val initializer: OptionalInitializer<T>) : OptionalInitializer<R> {

    override fun addMiddleware(middleware: Middleware<R?>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            middleware({ getState() as R }, dispatch, action, next)
        }
    }

    override fun addReducer(reducer: Reducer<R>) {
        initializer.addReducer { state, action ->
            reducer(state as R, action) as T
        }
    }
}