package io.lamart.kostore.composition

import io.lamart.kostore.Initializer
import io.lamart.kostore.Middleware
import io.lamart.kostore.OptionalInitializer
import io.lamart.kostore.Reducer

fun <T, R> Initializer<T>.compose(
        map: T.() -> R,
        reduce: T.(R) -> T,
        block: Initializer<R>.() -> Unit
) = compose(map, reduce).run(block)

fun <T, R> Initializer<T>.compose(
        map: T.() -> R,
        reduce: T.(R) -> T
): Initializer<R> = ComposeInitializer(this, map, reduce)

fun <T, R> OptionalInitializer<T>.compose(
        map: T.() -> R,
        reduce: T.(R) -> T,
        block: OptionalInitializer<R>.() -> Unit
) = compose(map, reduce).run(block)

fun <T, R> OptionalInitializer<T>.compose(
        map: T.() -> R,
        reduce: T.(R) -> T
): OptionalInitializer<R> = ComposeOptionalInitializer(this, map, reduce)

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