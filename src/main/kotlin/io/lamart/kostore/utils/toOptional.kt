package io.lamart.kostore.composition

import io.lamart.kostore.Initializer
import io.lamart.kostore.Middleware
import io.lamart.kostore.OptionalInitializer
import io.lamart.kostore.Reducer

fun <T> Initializer<T>.toOptional() = ToOptionalOptionalInitializer(this)

class ToOptionalOptionalInitializer<T>(private val initializer: Initializer<T>) : OptionalInitializer<T> {

    override fun addMiddleware(middleware: Middleware<T?>) = initializer.addMiddleware(middleware)

    override fun addReducer(reducer: Reducer<T>) = initializer.addReducer(reducer)

}