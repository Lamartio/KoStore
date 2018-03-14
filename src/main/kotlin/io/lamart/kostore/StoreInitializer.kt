package io.lamart.kostore


interface StoreInitializer<T> {

    infix fun addMiddleware(middleware: Middleware<T>)

    infix fun addReducer(reducer: Reducer<T>)

}

fun <T, R> StoreInitializer<T>.compose(get: (T) -> R, reduce: T.(R) -> T, init: StoreInitializer<R>.() -> Unit) =
        compose(get, reduce).run(init)

fun <T, R> StoreInitializer<T>.compose(map: (T) -> R, reduce: T.(R) -> T): StoreInitializer<R> = let { context ->
    object : StoreInitializer<R> {

        override fun addReducer(reducer: Reducer<R>) =
                reducer.compose(map, reduce).let(context::addReducer)

        override fun addMiddleware(middleware: Middleware<R>) =
                middleware.compose(map).let(context::addMiddleware)

    }
}