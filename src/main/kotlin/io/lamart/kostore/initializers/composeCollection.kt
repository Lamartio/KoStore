package io.lamart.kostore.initializers

import io.lamart.kostore.*

fun <T> Initializer<Collection<T>>.composeCollection(predicate: (state: T, action: Any) -> Boolean, block: OptionalInitializer<T>.() -> Unit) =
        composeCollection(predicate).run(block)

fun <T> Initializer<Collection<T>>.composeCollection(predicate: (state: T, action: Any) -> Boolean): OptionalInitializer<T> =
        composeCollection(this, { filter(it) }, { filter(it) }, predicate).toOptionalInitializer()

inline fun <T, reified A : Any> FilteredInitializer<Collection<T>, A>.composeFilteredCollection(crossinline predicate: (state: T, action: A) -> Boolean, block: FilteredOptionalInitializer<T, A> .() -> Unit) =
        composeFilteredCollection(predicate).let(block)

inline fun <T, reified A : Any> FilteredInitializer<Collection<T>, A>.composeFilteredCollection(crossinline predicate: (state: T, action: A) -> Boolean): FilteredOptionalInitializer<T, A> =
        composeCollection(this, { filter(it) }, { filter(it) }, predicate)

inline fun <T, reified A : Any> composeCollection(
        initializer: FilteredInitializer<Collection<T>, A>,
        crossinline transformMiddleware: (FilteredMiddleware<T?, A>) -> FilteredMiddleware<T?, A>,
        crossinline transformReducer: (FilteredReducer<T, A>) -> FilteredReducer<T, A>,
        crossinline predicate: (state: T, action: A) -> Boolean
): FilteredOptionalInitializer<T, A> = object : FilteredOptionalInitializer<T, A> {


    override fun addMiddleware(middleware: FilteredMiddleware<T?, A>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            transformMiddleware(middleware).invoke({ getState().find { predicate(it, action) } }, dispatch, action, next)
        }
    }

    override fun addReducer(reducer: FilteredReducer<T, A>) {
        initializer.addReducer { state: Collection<T>, action ->
            val item = state.find { predicate(it, action) }

            if (item == null)
                state
            else
                state.toMutableList().also { collection ->
                    collection.remove(item)
                    collection.add(transformReducer(reducer).invoke(item, action))
                }
        }
    }


}