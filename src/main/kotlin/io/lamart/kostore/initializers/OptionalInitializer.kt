package io.lamart.kostore.initializers

import io.lamart.kostore.Middleware
import io.lamart.kostore.Reducer


interface Initializer<T> {

    fun addMiddleware(middleware: Middleware<T>)

    fun addReducer(reducer: Reducer<T>)

}


interface OptionalInitializer<T> {

    fun addMiddleware(middleware: Middleware<T?>)

    fun addReducer(reducer: Reducer<T>)
}