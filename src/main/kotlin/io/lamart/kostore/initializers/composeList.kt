package io.lamart.kostore.initializers

import io.lamart.kostore.*

fun <T> Initializer<List<T>>.composeList(predicate: (state: T, action: Any) -> Boolean, block: OptionalInitializer<T>.() -> Unit) =
        composeList(predicate).run(block)

fun <T> Initializer<List<T>>.composeList(predicate: (state: T, action: Any) -> Boolean): OptionalInitializer<T> =
        composeFilteredList(predicate).toOptionalInitializer()

inline fun <T, reified A : Any> FilteredInitializer<List<T>, A>.composeFilteredList(crossinline predicate: (state: T, action: Any) -> Boolean, block: FilteredOptionalInitializer<T, A>.() -> Unit) =
        composeFilteredList(predicate).run(block)

inline fun <T, reified A : Any> FilteredInitializer<List<T>, A>.composeFilteredList(crossinline predicate: (state: T, action: Any) -> Boolean): FilteredOptionalInitializer<T, A> =
        object : FilteredOptionalInitializer<T, A> {

            val initializer = this@composeFilteredList

            override fun addMiddleware(middleware: FilteredMiddleware<T?, A>) {
                initializer.addMiddleware { getState, dispatch, action, next ->
                    filter(middleware).invoke({ getState().find { predicate(it, action) } }, dispatch, action, next)
                }
            }

            override fun addReducer(reducer: FilteredReducer<T, A>) {
                initializer.addReducer { state: List<T>, action ->
                    val index = state.indexOfFirst { predicate(it, action) }

                    if (index != -1)
                        state.toMutableList().also { list -> list[index] = filter(reducer).invoke(list[index], action) }
                    else
                        state
                }
            }

        }