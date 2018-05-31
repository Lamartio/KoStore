package io.lamart.kostore.initializers

import io.lamart.kostore.*


fun <K, V> Initializer<Map<K, V>>.composeMap(getKey: (action: Any) -> K, block: OptionalInitializer<V>.() -> Unit) =
        composeMap(getKey).run(block)

fun <K, V> Initializer<Map<K, V>>.composeMap(getKey: (action: Any) -> K): OptionalInitializer<V> =
        composeMap(this, { it }, { it }, getKey).toOptionalInitializer()

inline fun <K, V, reified A : Any> FilteredInitializer<Map<K, V>, A>.composeFilteredMap(crossinline getKey: (action: A) -> K, block: FilteredOptionalInitializer<V, A>.() -> Unit) =
        composeFilteredMap(getKey).run(block)

inline fun <K, V, reified A : Any> FilteredInitializer<Map<K, V>, A>.composeFilteredMap(crossinline getKey: (action: A) -> K): FilteredOptionalInitializer<V, A> =
        composeMap(this, { filter(it) }, { filter(it) }, getKey)

inline fun <K, V, reified A : Any> composeMap(
        initializer: FilteredInitializer<Map<K, V>, A>,
        crossinline transformMiddleware: (FilteredMiddleware<V?, A>) -> FilteredMiddleware<V?, A>,
        crossinline transformReducer: (FilteredReducer<V, A>) -> FilteredReducer<V, A>,
        crossinline getKey: (action: A) -> K
): FilteredOptionalInitializer<V, A> = object : FilteredOptionalInitializer<V, A> {

    override fun addMiddleware(middleware: FilteredMiddleware<V?, A>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            transformMiddleware(middleware).invoke({ getState()[getKey(action)] }, dispatch, action, next)
        }
    }

    override fun addReducer(reducer: FilteredReducer<V, A>) {
        initializer.addReducer { state, action ->
            val key = getKey(action)
            val value = state[key]

            if (value != null)
                state.toMutableMap().apply { put(key, transformReducer(reducer).invoke(value, action)) }
            else
                state
        }
    }

}