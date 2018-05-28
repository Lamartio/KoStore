package io.lamart.kostore.utils

import io.lamart.kostore.Middleware
import io.lamart.kostore.Reducer
import io.lamart.kostore.StoreInitializer

fun <K, V> mapInitializer(
        middleware: Middleware<V?> = { _, _, action, next -> next(action) },
        reducer: Reducer<V> = { state, _ -> state },
        predicate: (K, V) -> Boolean
): StoreInitializer<Map<K, V>>.() -> Unit = {

    addMiddleware { getState, dispatch, action, next ->
        middleware({ findValue(getState, predicate) }, dispatch, action, next)
    }

    addReducer { state: Map<K, V>, action ->
        val entry = state.entries.find { entry -> predicate(entry.key, entry.value) }

        if (entry != null)
            state.toMutableMap().also { map -> map[entry.key] = reducer(entry.value, action) }
        else
            state
    }

}

private fun <K, V> findValue(getState: () -> Map<K, V>, predicate: (K, V) -> Boolean): V? =
        getState()
                .entries
                .find { entry -> predicate(entry.key, entry.value) }
                ?.value
