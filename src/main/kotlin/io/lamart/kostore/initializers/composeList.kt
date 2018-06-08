package io.lamart.kostore.initializers

import io.lamart.kostore.*

fun <T> Initializer<List<T>>.composeList(
        predicate: (state: T, action: Any) -> Boolean
) = toOptionalInitializer().composeFilteredList(predicate).asOptionalInitializer()

fun <T> Initializer<List<T>>.composeList(
        predicate: (state: T, action: Any) -> Boolean,
        block: OptionalInitializer<T>.() -> Unit
) = composeList(predicate).run(block)

inline fun <T, reified A : Any> FilteredInitializer<List<T>, A>.composeFilteredList(
        crossinline predicate: (state: T, action: A) -> Boolean,
        block: FilteredOptionalInitializer<T, A>.() -> Unit
) = composeFilteredList(predicate).run(block)

inline fun <T, reified A : Any> FilteredInitializer<List<T>, A>.composeFilteredList(
        crossinline predicate: (state: T, action: A) -> Boolean
) = toOptionalInitializer().composeFilteredList(predicate)

fun <T> OptionalInitializer<List<T>>.composeList(
        predicate: (state: T, action: Any) -> Boolean,
        block: OptionalInitializer<T>.() -> Unit
) = composeFilteredList(predicate).asOptionalInitializer().run(block)

fun <T> OptionalInitializer<List<T>>.composeList(
        predicate: (state: T, action: Any) -> Boolean
) = composeFilteredList(predicate).asOptionalInitializer()

inline fun <T, reified A : Any> FilteredOptionalInitializer<List<T>, A>.composeFilteredList(
        crossinline predicate: (state: T, action: A) -> Boolean,
        block: FilteredOptionalInitializer<T, A>.() -> Unit
) = composeFilteredList(predicate).apply(block)

inline fun <T, reified A : Any> FilteredOptionalInitializer<List<T>, A>.composeFilteredList(
        crossinline predicate: (state: T, action: A) -> Boolean
) = composeFilteredList(this, { filter(it) }, { filter(it) }, predicate)

inline fun <T, reified A : Any> composeFilteredList(
        initializer: FilteredOptionalInitializer<List<T>, A>,
        crossinline transformMiddleware: (FilteredMiddleware<T?, A>) -> FilteredMiddleware<T?, A>,
        crossinline transformReducer: (FilteredReducer<T, A>) -> FilteredReducer<T, A>,
        crossinline predicate: (state: T, action: A) -> Boolean
): FilteredOptionalInitializer<T, A> = object : FilteredOptionalInitializer<T, A> {

    override fun addMiddleware(middleware: FilteredMiddleware<T?, A>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            transformMiddleware(middleware).invoke({ getState()?.find { predicate(it, action) } }, dispatch, action, next)
        }
    }

    override fun addReducer(reducer: FilteredReducer<T, A>) {
        initializer.addReducer { state: List<T>, action ->
            val index = state.indexOfFirst { predicate(it, action) }

            if (index != -1)
                state.toMutableList().also { list -> list[index] = transformReducer(reducer).invoke(list[index], action) }
            else
                state
        }
    }

}