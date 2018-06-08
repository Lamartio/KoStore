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


/*
    The optional family
 */


inline fun <T, R> OptionalInitializer<T>.composeOptional(
        crossinline map: (T) -> R,
        crossinline reduce: T.(R) -> T,
        block: OptionalInitializer<R>.() -> Unit
) = composeOptional(this, { filter(it) }, { filter(it) }, map, reduce).toOptionalInitializer().run(block)

inline fun <T, R> OptionalInitializer<T>.composeOptional(
        crossinline map: (T) -> R,
        crossinline reduce: T.(R) -> T
): OptionalInitializer<R> = composeOptional(this, { filter(it) }, { filter(it) }, map, reduce).toOptionalInitializer()

inline fun <T, R, reified A : Any> FilteredOptionalInitializer<T, A>.composeFilteredOptional(
        crossinline map: (T) -> R,
        crossinline reduce: T.(R) -> T,
        block: FilteredOptionalInitializer<R, A>.() -> Unit
) = composeOptional(this, { filter(it) }, { filter(it) }, map, reduce).run(block)

inline fun <T, R, reified A : Any> FilteredOptionalInitializer<T, A>.composeFilteredOptional(
        crossinline map: (T) -> R,
        crossinline reduce: T.(R) -> T
): FilteredOptionalInitializer<R, A> = composeOptional(this, { filter(it) }, { filter(it) }, map, reduce)

inline fun <T, R, reified A : Any> composeOptional(
        initializer: FilteredOptionalInitializer<T, A>,
        crossinline transformMiddleware: (FilteredMiddleware<R?, A>) -> FilteredMiddleware<R?, A>,
        crossinline transformReducer: (FilteredReducer<R, A>) -> FilteredReducer<R, A>,
        crossinline map: (T) -> R,
        crossinline reduce: T.(R) -> T
): FilteredOptionalInitializer<R, A> =
        object : FilteredOptionalInitializer<R, A> {

            override fun addMiddleware(middleware: FilteredMiddleware<R?, A>) =
                    initializer.addMiddleware { getState, dispatch, action, next ->
                        transformMiddleware(middleware).invoke(
                                { getState()?.let(map) },
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
