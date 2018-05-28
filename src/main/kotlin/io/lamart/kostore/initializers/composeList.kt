package io.lamart.kostore.initializers

import io.lamart.kostore.Initializer
import io.lamart.kostore.Middleware
import io.lamart.kostore.OptionalInitializer
import io.lamart.kostore.Reducer

fun <T> Initializer<List<T>>.composeList(predicate: (state: T, action: Any) -> Boolean, block: OptionalInitializer<T>.() -> Unit) =
        composeList(predicate).run(block)

fun <T> Initializer<List<T>>.composeList(predicate: (state: T, action: Any) -> Boolean): OptionalInitializer<T> =
        object : OptionalInitializer<T> {

            val initializer = this@composeList

            override fun addOptionalMiddleware(middleware: Middleware<T?>) {
                initializer.addMiddleware { getState, dispatch, action, next ->
                    middleware({ getState().find { predicate(it, action) } }, dispatch, action, next)
                }
            }

            override fun addReducer(reducer: Reducer<T>) {
                initializer.addReducer { state: List<T>, action ->
                    val index = state.indexOfFirst { predicate(it, action) }

                    if (index != -1)
                        state.toMutableList().also { list -> list[index] = reducer(list[index], action) }
                    else
                        state
                }
            }
        }