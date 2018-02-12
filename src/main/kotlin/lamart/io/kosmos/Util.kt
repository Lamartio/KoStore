package lamart.io.kosmos

internal object StoreUtil {

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

    fun <T> combineListeners(previous: (T) -> Unit, next: (T) -> Unit): (T) -> Unit = {
        previous(it)
        next(it)
    }

}