package io.lamart.kostore.composition

import io.lamart.kostore.Initializer
import io.lamart.kostore.Middleware
import io.lamart.kostore.OptionalInitializer
import io.lamart.kostore.Reducer

fun <T> Initializer<T>.mapAction(mapAction: (Any) -> Any, block: Initializer<T>.() -> Unit) =
        this.mapAction(mapAction).run(block)

fun <T> Initializer<T>.mapAction(mapAction: (Any) -> Any): Initializer<T> =
        MapActionInitializer(this, mapAction)

fun <T> OptionalInitializer<T>.mapAction(mapAction: (Any) -> Any, block: OptionalInitializer<T>.() -> Unit) =
        this.mapAction(mapAction).run(block)

fun <T> OptionalInitializer<T>.mapAction(mapAction: (Any) -> Any): OptionalInitializer<T> =
        MapActionOptionalInitializer(this, mapAction)

class MapActionInitializer<T>(
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

class MapActionOptionalInitializer<T>(
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