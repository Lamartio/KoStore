package io.lamart.kosmos

internal object Functions {

    fun emptyReducer(state: Int, action: Any): Int = state

    fun mathReducer(state: Int, action: Any): Int =
            when (action) {
                "increment" -> state + 1
                "decrement" -> state - 1
                else -> state
            }

    fun emptyMiddleware(store: StoreSource<Int>, action: Any, next: (Any) -> Unit) = next(action)

    fun flipMathMiddleware(store: StoreSource<Int>, action: Any, next: (Any) -> Unit) {
        val newAction = when (action) {
            "increment" -> "decrement"
            "decrement" -> "increment"
            else -> action
        }

        next(newAction)
    }

    fun multiEmitMiddleware(store: StoreSource<Int>, action: Any, next: (Any) -> Unit) {
        next(action)
        next(action)
    }

    fun logMiddleware(store: StoreSource<Int>, action: Any, next: (Any) -> Unit) {
        if (action is LogEntry) {
            next(action)
        } else {
            val before = store.state
            next(action)
            val after = store.state

            store(LogEntry(before, action, after))
        }
    }

    data class LogEntry(val before: Int, val action: Any, val after: Int)

}