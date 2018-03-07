package io.lamart.kosmos


interface StoreInitializer<T> {

    infix fun addMiddleware(middleware: Middleware<T>)

    infix fun addReducer(reducer: Reducer<T>)

}

fun <T, R> StoreInitializer<T>.compose(get: (T) -> R, set: T.(R) -> T, init: StoreInitializer<R>.() -> Unit) =
        compose(get, set).run(init)

fun <T, R> StoreInitializer<T>.compose(get: (T) -> R, create: T.(R) -> T): StoreInitializer<R> = let { context ->
    object : StoreInitializer<R> {

        override fun addReducer(reducer: Reducer<R>) =
                reducer.compose(get, create).let(context::addReducer)

        override fun addMiddleware(middleware: Middleware<R>) =
                middleware.compose(get).let(context::addMiddleware)

    }
}