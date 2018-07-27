package io.lamart.kostore.composition

import io.lamart.kostore.*
import io.lamart.kostore.utility.toOptional

fun <T> Initializer<Set<T>>.composeSet(
        predicate: (state: T, action: Any) -> Boolean,
        block: OptionalInitializer<T>.() -> Unit
) = composeSet(predicate).run(block)

fun <T> Initializer<Set<T>>.composeSet(predicate: (state: T, action: Any) -> Boolean): OptionalInitializer<T> =
        ComposeSetOptionalInitializer(this, predicate)

fun <T> OptionalInitializer<Set<T>>.composeSet(
        predicate: (state: T, action: Any) -> Boolean,
        block: OptionalInitializer<T>.() -> Unit
) = composeSet(predicate).run(block)

fun <T> OptionalInitializer<Set<T>>.composeSet( predicate: (state: T, action: Any) -> Boolean): OptionalInitializer<T> =
        ComposeOptionalSetOptionalInitializer(this, predicate)

class ComposeSetOptionalInitializer<T>(
        private val initializer: Initializer<Set<T>>,
        private val predicate: (state: T, action: Any) -> Boolean,
        private val toMutable: (Set<T>) -> MutableSet<T> = { it.toMutableSet() }
) : OptionalInitializer<T> by ComposeOptionalSetOptionalInitializer(initializer.toOptional(), predicate, toMutable)

class ComposeOptionalSetOptionalInitializer<T>(
        private val initializer: OptionalInitializer<Set<T>>,
        private val predicate: (state: T, action: Any) -> Boolean,
        private val toMutable: (Set<T>) -> MutableSet<T> = { it.toMutableSet() }
) : OptionalInitializer<T> {

    override fun addMiddleware(middleware: Middleware<T?>) {
        initializer.addMiddleware { getState, dispatch, action, next ->
            middleware({ getState()?.toList()?.find { predicate(it, action) } }, dispatch, action, next)
        }
    }

    override fun addReducer(reducer: Reducer<T>) {
        initializer.addReducer { set, action ->
            val element = set.find { predicate(it, action) }

            if (element != null)
                toMutable(set).apply { remove(element);add(reducer(element, action)) }
            else
                set
        }
    }

}