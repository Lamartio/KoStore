package io.lamart.kostore.operators

import io.lamart.kostore.*
import io.lamart.kostore.utility.toOptional

fun <T> Initializer<List<T>>.composeList(
        predicate: (state: T, action: Any) -> Boolean,
        block: OptionalInitializer<T>.() -> Unit
) = composeList(predicate).run(block)

fun <T> Initializer<List<T>>.composeList(predicate: (state: T, action: Any) -> Boolean): OptionalInitializer<T> =
        ComposeListInitializer(this, predicate)

fun <T> OptionalInitializer<List<T>>.composeList(
        predicate: (state: T, action: Any) -> Boolean,
        block: OptionalInitializer<T>.() -> Unit
) = composeList(predicate).run(block)

fun <T> OptionalInitializer<List<T>>.composeList(predicate: (state: T, action: Any) -> Boolean): OptionalInitializer<T> =
        ComposeListOptionalInitializer(this, predicate)

class ComposeListInitializer<T>(
        private val initializer: Initializer<List<T>>,
        private val predicate: (state: T, action: Any) -> Boolean,
        private val toMutable: (List<T>) -> MutableList<T> = { it.toMutableList() }
) : OptionalInitializer<T> by ComposeListOptionalInitializer(initializer.toOptional(), predicate, toMutable)

class ComposeListOptionalInitializer<T>(
        private val initializer: OptionalInitializer<List<T>>,
        private val predicate: (state: T, action: Any) -> Boolean,
        private val toMutable: (List<T>) -> MutableList<T> = { it.toMutableList() }
) : OptionalInitializer<T> {

    override fun addMiddleware(middleware: Middleware<T?>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            middleware(
                    { getState()?.toList()?.find { predicate(it, action) } },
                    dispatch,
                    action,
                    next
            )
        }
    }

    override fun addReducer(reducer: Reducer<T>) {
        initializer.addReducer { list, action ->
            list.indexOfFirst { predicate(it, action) }.let { index ->
                when {
                    index != -1 -> toMutable(list).apply { set(index, reducer(get(index), action)) }
                    else -> list
                }
            }
        }
    }

}
