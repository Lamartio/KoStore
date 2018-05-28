package io.lamart.kostore


interface Initializer<T> {

    fun addMiddleware(middleware: Middleware<T>)

    fun addReducer(reducer: Reducer<T>)

}

interface OptionalInitializer<T> : Initializer<T> {

    /**
     * Use this with caution! It could be that no item could be found or that the item is removed during asynchronous actions.
     */

    override fun addMiddleware(middleware: Middleware<T>) {
        addOptionalMiddleware { getState, dispatch, action, next ->
            middleware({ getState()!! }, dispatch, action, next)
        }
    }

    fun addOptionalMiddleware(middleware: Middleware<T?>)

    override fun addReducer(reducer: Reducer<T>)
}