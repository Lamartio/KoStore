package io.lamart.kostore


interface OptionalInitializer<T> {

    fun addMiddleware(middleware: Middleware<T?>)

    fun addReducer(reducer: Reducer<T>)

}