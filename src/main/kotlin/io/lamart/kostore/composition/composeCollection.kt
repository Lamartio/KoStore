package io.lamart.kostore.composition

import io.lamart.kostore.*
import io.lamart.kostore.utility.toOptional

fun <T> Initializer<Collection<T>>.composeCollection(
        predicate: (state: T, action: Any) -> Boolean,
        block: OptionalInitializer<T>.() -> Unit
) = composeCollection(predicate).run(block)

fun <T> Initializer<Collection<T>>.composeCollection(predicate: (state: T, action: Any) -> Boolean): OptionalInitializer<T> =
        ComposeCollectionOptionalInitializer(this, predicate)

fun <T> OptionalInitializer<Collection<T>>.composeCollection(
        predicate: (state: T, action: Any) -> Boolean,
        block: OptionalInitializer<T>.() -> Unit
) = composeCollection(predicate).run(block)

fun <T> OptionalInitializer<Collection<T>>.composeCollection(predicate: (state: T, action: Any) -> Boolean): OptionalInitializer<T> =
        ComposeOptionalCollectionOptionalInitializer(this, predicate)

class ComposeCollectionOptionalInitializer<T>(
        private val initializer: Initializer<Collection<T>>,
        private val predicate: (state: T, action: Any) -> Boolean,
        private val toMutable: (Collection<T>) -> MutableCollection<T> = { it.toMutableSet() }
) : OptionalInitializer<T> by ComposeOptionalCollectionOptionalInitializer(initializer.toOptional(), predicate, toMutable)

class ComposeOptionalCollectionOptionalInitializer<T>(
        private val initializer: OptionalInitializer<Collection<T>>,
        private val predicate: (state: T, action: Any) -> Boolean,
        private val toMutable: (Collection<T>) -> MutableCollection<T> = { it.toMutableSet() }
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