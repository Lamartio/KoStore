package io.lamart.kostore.composition

import io.lamart.kostore.*
import io.lamart.kostore.utility.toOptional

fun <T> Initializer<List<T>>.composeList(
        predicate: (state: T, action: Any) -> Boolean,
        block: OptionalInitializer<T>.() -> Unit
) = composeList(predicate).run(block)

fun <T> Initializer<List<T>>.composeList(predicate: (state: T, action: Any) -> Boolean): OptionalInitializer<T> =
        ComposeListOptionalInitializer(this, predicate)

fun <T> OptionalInitializer<List<T>>.composeList(
        predicate: (state: T, action: Any) -> Boolean,
        block: OptionalInitializer<T>.() -> Unit
) = composeList(predicate).run(block)

fun <T> OptionalInitializer<List<T>>.composeList(predicate: (state: T, action: Any) -> Boolean): OptionalInitializer<T> =
        ComposeOptionalListOptionalInitializer(this, predicate)

class ComposeListOptionalInitializer<T>(
        private val initializer: Initializer<List<T>>,
        private val predicate: (state: T, action: Any) -> Boolean,
        private val toMutable: (List<T>) -> MutableList<T> = { it.toMutableList() }
) : OptionalInitializer<T> by ComposeOptionalListOptionalInitializer(initializer.toOptional(), predicate, toMutable)

class ComposeOptionalListOptionalInitializer<T>(
        private val initializer: OptionalInitializer<List<T>>,
        private val predicate: (state: T, action: Any) -> Boolean,
        private val toMutable: (List<T>) -> MutableList<T> = { it.toMutableList() }
) : OptionalInitializer<T> {

    override fun addMiddleware(middleware: Middleware<T?>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            middleware({ getState()?.toList()?.find { predicate(it, action) } }, dispatch, action, next)
        }
    }

    override fun addReducer(reducer: Reducer<T>) {
        initializer.addReducer { list, action ->
            val index = list.indexOfFirst { predicate(it, action) }

            if (index != -1)
                toMutable(list).apply { set(index, reducer(get(index), action)) }
            else
                list
        }
    }

}
