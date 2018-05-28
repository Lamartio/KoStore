package io.lamart.kostore.initializers

import io.lamart.kostore.Initializer
import io.lamart.kostore.Middleware
import io.lamart.kostore.OptionalInitializer
import io.lamart.kostore.Reducer

fun <T> Initializer<Collection<T>>.composeCollection(predicate: (state: T, action: Any) -> Boolean, block: OptionalInitializer<T>.() -> Unit) =
        composeCollection(predicate).run(block)

fun <T> Initializer<Collection<T>>.composeCollection(predicate: (state: T, action: Any) -> Boolean): OptionalInitializer<T> =
        object : OptionalInitializer<T> {

            val initializer = this@composeCollection

            override fun addOptionalMiddleware(middleware: Middleware<T?>) {
                initializer.addMiddleware { getState, dispatch, action, next ->
                    middleware({ getState().find { predicate(it, action) } }, dispatch, action, next)
                }
            }

            override fun addReducer(reducer: Reducer<T>) {
                initializer.addReducer { state: Collection<T>, action ->
                    val item = state.find { predicate(it, action) }

                    if (item == null)
                        state
                    else
                        state.toMutableList().also { collection ->
                            collection.remove(item)
                            collection.add(reducer(item, action))
                        }
                }
            }
        }