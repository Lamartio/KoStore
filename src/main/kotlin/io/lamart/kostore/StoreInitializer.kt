package io.lamart.kostore


interface StoreInitializer<T> {

    fun addMiddleware(middleware: Middleware<T>)

    fun addReducer(reducer: Reducer<T>)

}

fun <T, R> StoreInitializer<T>.compose(map: (T) -> R, reduce: T.(R) -> T, init: StoreInitializer<R>.() -> Unit) =
        compose(map, reduce).run(init)

fun <T, R> StoreInitializer<T>.compose(map: (T) -> R, reduce: T.(R) -> T): StoreInitializer<R> =
        object : StoreInitializer<R> {

            override fun addMiddleware(middleware: Middleware<R>) =
                    middleware.compose(map).let(this@compose::addMiddleware)

            override fun addReducer(reducer: Reducer<R>) =
                    reducer.compose(map, reduce).let(this@compose::addReducer)

        }

private fun <T, R> Middleware<T>.compose(map: (R) -> T): Middleware<R> =
        { getState, dispatch, action, next ->
            invoke(
                    { getState().let(map) },
                    dispatch,
                    action,
                    next
            )
        }

private fun <T, R> Reducer<T>.compose(map: (R) -> T, reduce: R.(T) -> R): Reducer<R> =
        { state: R, action: Any ->
            map(state)
                    .let { invoke(it, action) }
                    .let { state.reduce(it) }
        }