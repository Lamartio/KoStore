package io.lamart.kostore.initializers

import io.lamart.kostore.*

fun <T, R> Initializer<T>.compose(
        map: (T) -> R,
        reduce: T.(R) -> T,
        block: Initializer<R>.() -> Unit
) = compose(map, reduce).run(block)

fun <T, R> Initializer<T>.compose(map: (T) -> R, reduce: T.(R) -> T): Initializer<R> =
        compose(this, { it }, { it }, map, reduce).toInitializer()

inline fun <T, R, reified A : Any> FilteredInitializer<T, A>.composeFiltered(
        crossinline map: (T) -> R,
        crossinline reduce: T.(R) -> T
): FilteredInitializer<R, A> = compose(this, { filter(it) }, { filter(it) }, map, reduce)

inline fun <T, R, reified A : Any> compose(
        initializer: FilteredInitializer<T, A>,
        crossinline transformMiddleware: (FilteredMiddleware<R, A>) -> FilteredMiddleware<R, A>,
        crossinline transformReducer: (FilteredReducer<R, A>) -> FilteredReducer<R, A>,
        crossinline map: (T) -> R,
        crossinline reduce: T.(R) -> T
): FilteredInitializer<R, A> =
        object : FilteredInitializer<R, A> {

            override fun addMiddleware(middleware: FilteredMiddleware<R, A>) =
                    initializer.addMiddleware { getState, dispatch, action, next ->
                        transformMiddleware(middleware).invoke(
                                { getState().let(map) },
                                dispatch,
                                action,
                                next
                        )
                    }

            override fun addReducer(reducer: FilteredReducer<R, A>) =
                    initializer.addReducer { state, action ->
                        map(state)
                                .let { transformReducer(reducer).invoke(it, action) }
                                .let { state.reduce(it) }
                    }

        }
