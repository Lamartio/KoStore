package io.lamart.kosmos


interface StoreInitializer<T> {

    fun addReducer(reducer: Reducer<T>)

    fun addMiddleware(middleware: Middleware<T>)

}

fun <I, O> StoreInitializer<O>.compose(get: (O) -> I, set: O.(I) -> O, init: StoreInitializer<I>.() -> Unit) =
        compose(get, set).run(init)

fun <I, O> StoreInitializer<O>.compose(get: (O) -> I, create: O.(I) -> O): StoreInitializer<I> = let { context ->
    object : StoreInitializer<I> {

        override fun addReducer(reducer: Reducer<I>) =
                reducer.compose(get, create).let(context::addReducer)

        override fun addMiddleware(middleware: Middleware<I>) =
                middleware.compose(get).let(context::addMiddleware)

    }
}