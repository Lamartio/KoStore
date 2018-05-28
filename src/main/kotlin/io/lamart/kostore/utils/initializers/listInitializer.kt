package io.lamart.kostore.utils.initializers

import io.lamart.kostore.Middleware
import io.lamart.kostore.Reducer
import io.lamart.kostore.StoreInitializer

fun <T> listInitializer(
        middleware: Middleware<T?> = { _, _, action, next -> next(action) },
        reducer: Reducer<T> = { state, _ -> state },
        predicate: (T, Any) -> Boolean
): StoreInitializer<List<T>>.() -> Unit = {

    addMiddleware { getState, dispatch, action, next ->
        middleware({ getState().find { predicate(it, action) } }, dispatch, action, next)
    }

    addReducer { state: List<T>, action ->
        val index = state.indexOfFirst { predicate(it, action) }

        if (index != -1)
            state.toMutableList().also { list -> list[index] = reducer(list[index], action) }
        else
            state
    }

}

