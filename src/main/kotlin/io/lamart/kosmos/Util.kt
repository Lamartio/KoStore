package io.lamart.kosmos

internal object Util {

    fun <T> combineMiddlewares(
            previous: (Store<T>, Any, (Any) -> Unit) -> Unit,
            next: (Store<T>, Any, (Any) -> Unit) -> Unit
    ): (Store<T>, Any, (Any) -> Unit) -> Unit =
            { getState, action, next ->
                previous(getState, action, { action ->
                    next(getState, action, next)
                })
            }

    fun <T> combineReducers(previous: (T, Any) -> T, next: (T, Any) -> T): (T, Any) -> T =
            { state, action -> previous(state, action).let { next(it, action) } }

    fun <T> wrapObservers(observers: Iterable<(T) -> Unit>): (T) -> Unit {
        var result = { state: T -> }

        observers.forEach { next -> result = result.let { previous -> { previous(it); next(it) } } }

        return result
    }

}