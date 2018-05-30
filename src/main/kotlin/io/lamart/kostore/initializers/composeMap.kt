package io.lamart.kostore.initializers

import io.lamart.kostore.*


fun <K, V> Initializer<Map<K, V>>.composeMap(getKey: (action: Any) -> K, block: OptionalInitializer<V>.() -> Unit) =
        composeMap(getKey).run(block)

fun <K, V> Initializer<Map<K, V>>.composeMap(getKey: (action: Any) -> K): OptionalInitializer<V> =
        composeFilteredMap(getKey).toOptionalInitializer()

inline fun <K, V, reified A : Any> FilteredInitializer<Map<K, V>, A>.composeFilteredMap(crossinline getKey: (action: Any) -> K, block: FilteredOptionalInitializer<V, A>.() -> Unit) =
        composeFilteredMap(getKey).run(block)

inline fun <K, V, reified A : Any> FilteredInitializer<Map<K, V>, A>.composeFilteredMap(crossinline getKey: (action: Any) -> K): FilteredOptionalInitializer<V, A> =
        object : FilteredOptionalInitializer<V, A> {

            val initializer = this@composeFilteredMap

            override fun addMiddleware(middleware: FilteredMiddleware<V?, A>) {
                initializer.addMiddleware { getState, dispatch, action, next ->
                    filter(middleware).invoke({ getState()[getKey(action)] }, dispatch, action, next)
                }
            }

            override fun addReducer(reducer: FilteredReducer<V, A>) {
                initializer.addReducer { state, action ->
                    val key = getKey(action)
                    val value = state[key]

                    if (value == null)
                        state
                    else
                        state.toMutableMap().apply {
                            put(key, filter(reducer).invoke(value, action))
                        }
                }
            }

        }