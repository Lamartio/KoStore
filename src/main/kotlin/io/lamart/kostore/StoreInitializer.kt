package io.lamart.kostore


interface StoreInitializer<T> {

    fun addMiddleware(middleware: Middleware<T>)

    fun addReducer(reducer: Reducer<T>)

}

interface FilteredStoreInitializer<T, out A> {

    fun addMiddleware(middleware: FilteredMiddleware<T, A>)

    fun addReducer(reducer: FilteredReducer<T, A>)

}

inline fun <T, reified A> StoreInitializer<T>.filter(block: FilteredStoreInitializer<T, A>.() -> Unit) = block(filter())

inline fun <T, reified A> StoreInitializer<T>.filter(): FilteredStoreInitializer<T, A> =
        object : FilteredStoreInitializer<T, A> {

            override fun addMiddleware(middleware: FilteredMiddleware<T, A>) =
                    this@filter.addMiddleware(filter(middleware))

            override fun addReducer(reducer: FilteredReducer<T, A>) =
                    this@filter.addReducer(filter(reducer))

        }

inline fun <T, reified A, R> StoreInitializer<T>.filteredCompose(
        noinline map: (T) -> R,
        noinline reduce: T.(R) -> T,
        block: FilteredStoreInitializer<R, A>.() -> Unit
) = compose(map, reduce).filter<R, A>().let(block)

fun <T, R> StoreInitializer<T>.compose(
        map: (T) -> R,
        reduce: T.(R) -> T,
        block: StoreInitializer<R>.() -> Unit
) = compose(map, reduce).run(block)

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