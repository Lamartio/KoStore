package io.lamart.kostore.operators

import io.lamart.kostore.Initializer
import io.lamart.kostore.Middleware
import io.lamart.kostore.OptionalInitializer
import io.lamart.kostore.Reducer

class MapInitializer<T>(
        private val initializer: Initializer<T>,
        private val mapAction: (Any) -> Any
) : Initializer<T> {

    override fun addMiddleware(middleware: Middleware<T>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            middleware(getState, dispatch, mapAction(action), next)
        }
    }

    override fun addReducer(reducer: Reducer<T>) {
        initializer.addReducer { state, action ->
            reducer(state, mapAction(action))
        }
    }

}

class MapOptionalInitializer<T>(
        private val initializer: OptionalInitializer<T>,
        private val mapAction: (Any) -> Any
) : OptionalInitializer<T> {

    override fun addMiddleware(middleware: Middleware<T?>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            middleware(getState, dispatch, mapAction(action), next)
        }
    }

    override fun addReducer(reducer: Reducer<T>) {
        initializer.addReducer { state, action ->
            reducer(state, mapAction(action))
        }
    }

}