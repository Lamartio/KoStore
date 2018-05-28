package io.lamart.kostore.initializers

import io.lamart.kostore.Initializer
import io.lamart.kostore.Middleware
import io.lamart.kostore.OptionalInitializer
import io.lamart.kostore.Reducer


fun <K, V> Initializer<Map<K, V>>.composeMap(getKey: (action: Any) -> K, block: OptionalInitializer<V>.() -> Unit) =
        composeMap(getKey).run(block)

fun <K, V> Initializer<Map<K, V>>.composeMap(getKey: (action: Any) -> K): OptionalInitializer<V> =
        object : OptionalInitializer<V> {

            val initializer = this@composeMap

            override fun addOptionalMiddleware(middleware: Middleware<V?>) {
                initializer.addMiddleware { getState, dispatch, action, next ->
                    middleware(
                            { getState()[getKey(action)] },
                            dispatch,
                            action,
                            next
                    )
                }
            }

            override fun addReducer(reducer: Reducer<V>) {
                initializer.addReducer { state, action ->
                    val key = getKey(action)
                    val value = state[key]

                    if (value == null)
                        state
                    else
                        state.toMutableMap().apply {
                            put(key, reducer(value, action))
                        }
                }
            }

        }