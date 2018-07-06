package io.lamart.kostore.composition

import io.lamart.kostore.*
import io.lamart.kostore.utility.toOptional

fun <K, V> Initializer<Map<K, V>>.composeMap(
        predicate: (key: K, action: Any) -> Boolean,
        block: OptionalInitializer<V>.() -> Unit
) = composeMap(predicate).run(block)

fun <K, V> Initializer<Map<K, V>>.composeMap(predicate: (key: K, action: Any) -> Boolean): OptionalInitializer<V> =
        ComposeMapOptionalInitializer(this, predicate)

fun <K, V> OptionalInitializer<Map<K, V>>.composeMap(
        predicate: (key: K, action: Any) -> Boolean,
        block: OptionalInitializer<V>.() -> Unit
) = composeMap(predicate).run(block)

fun <K, V> OptionalInitializer<Map<K, V>>.composeMap(predicate: (key: K, action: Any) -> Boolean): OptionalInitializer<V> =
        ComposeOptionalMapOptionalInitializer(this, predicate)

class ComposeMapOptionalInitializer<K, V>(
        private val initializer: Initializer<Map<K, V>>,
        private val predicate: (key: K, action: Any) -> Boolean,
        private val toMutable: (Map<K, V>) -> MutableMap<K, V> = { it.toMutableMap() }
) : OptionalInitializer<V> by ComposeOptionalMapOptionalInitializer(initializer.toOptional(), predicate, toMutable)

class ComposeOptionalMapOptionalInitializer<K, V>(
        private val initializer: OptionalInitializer<Map<K, V>>,
        private val predicate: (key: K, action: Any) -> Boolean,
        private val toMutable: (Map<K, V>) -> MutableMap<K, V> = { it.toMutableMap() }
) : OptionalInitializer<V> {

    override fun addMiddleware(middleware: Middleware<V?>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            middleware({ getState()?.entries?.find { predicate(it.key, action) }?.value }, dispatch, action, next)
        }
    }

    override fun addReducer(reducer: Reducer<V>) {
        initializer.addReducer { map, action ->
            val entry = map.entries.find { predicate(it.key, action) }

            if (entry != null)
                toMutable(map).apply { put(entry.key, reducer(entry.value, action)) }
            else
                map
        }
    }

}