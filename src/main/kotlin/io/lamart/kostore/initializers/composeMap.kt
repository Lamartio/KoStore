package io.lamart.kostore.initializers

import io.lamart.kostore.*


fun <K, V> Initializer<Map<K, V>>.composeMap(
        getKey: (action: Any) -> K,
        block: OptionalInitializer<V>.() -> Unit
) = toOptionalInitializer().composeFilteredMap(getKey).asOptionalInitializer().run(block)

fun <K, V> Initializer<Map<K, V>>.composeMap(
        getKey: (action: Any) -> K
) = toOptionalInitializer().composeFilteredMap(getKey).asOptionalInitializer()

inline fun <K, V, reified A : Any> FilteredInitializer<Map<K, V>, A>.composeFilteredMap(
        crossinline getKey: (action: A) -> K,
        block: FilteredOptionalInitializer<V, A>.() -> Unit
) = toOptionalInitializer().composeFilteredMap(getKey).run(block)

inline fun <K, V, reified A : Any> FilteredInitializer<Map<K, V>, A>.composeFilteredMap(
        crossinline getKey: (action: A) -> K
) = toOptionalInitializer().composeFilteredMap(getKey)

fun <K, V> OptionalInitializer<Map<K, V>>.composeMap(
        getKey: (action: Any) -> K,
        block: OptionalInitializer<V>.() -> Unit
) = composeFilteredMap(getKey).asOptionalInitializer().run(block)

fun <K, V> OptionalInitializer<Map<K, V>>.composeMap(
        getKey: (action: Any) -> K
) = composeFilteredMap(getKey).asOptionalInitializer()

inline fun <K, V, reified A : Any> FilteredOptionalInitializer<Map<K, V>, A>.composeFilteredMap(
        crossinline getKey: (action: A) -> K,
        block: FilteredOptionalInitializer<V, A>.() -> Unit = {}
) = composeFilteredMap(getKey).run(block)

inline fun <K, V, reified A : Any> FilteredOptionalInitializer<Map<K, V>, A>.composeFilteredMap(
        crossinline getKey: (action: A) -> K
) = composeFilteredMap(this, { filter(it) }, { filter(it) }, getKey)

inline fun <K, V, reified A : Any> composeFilteredMap(
        initializer: FilteredOptionalInitializer<Map<K, V>, A>,
        crossinline transformMiddleware: (FilteredMiddleware<V?, A>) -> FilteredMiddleware<V?, A>,
        crossinline transformReducer: (FilteredReducer<V, A>) -> FilteredReducer<V, A>,
        crossinline getKey: (action: A) -> K
): FilteredOptionalInitializer<V, A> = object : FilteredOptionalInitializer<V, A> {

    override fun addMiddleware(middleware: FilteredMiddleware<V?, A>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            transformMiddleware(middleware).invoke({ getState()?.get(getKey(action)) }, dispatch, action, next)
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