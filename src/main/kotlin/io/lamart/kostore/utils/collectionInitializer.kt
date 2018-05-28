package nl.elements.cerium.features.info

import io.lamart.kostore.Middleware
import io.lamart.kostore.Reducer
import io.lamart.kostore.StoreInitializer

fun <T> collectionInitializer(
        middleware: Middleware<T?> = { _, _, action, next -> next(action) },
        reducer: Reducer<T> = { state, _ -> state },
        predicate: (T) -> Boolean
): StoreInitializer<Collection<T>>.() -> Unit = {

    addMiddleware { getState, dispatch, action, next ->
        middleware({ getState().find(predicate) }, dispatch, action, next)
    }

    addReducer { state: Collection<T>, action ->
        val item = state.firstOrNull(predicate)

        if (item == null)
            state
        else
            state.toMutableSet().also { collection ->
                collection.remove(item)
                collection.add(reducer(item, action))
            }
    }

}