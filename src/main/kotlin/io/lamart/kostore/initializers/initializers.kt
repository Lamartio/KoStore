package io.lamart.kostore.initializers

import io.lamart.kostore.FilteredMiddleware
import io.lamart.kostore.FilteredReducer


interface Initializer<T> : FilteredInitializer<T, Any>

interface OptionalInitializer<T> : FilteredOptionalInitializer<T, Any>

interface FilteredInitializer<T, out A : Any> {

    fun addMiddleware(middleware: FilteredMiddleware<T, A>)

    fun addReducer(reducer: FilteredReducer<T, A>)

}

interface FilteredOptionalInitializer<T, out A : Any> {

    fun addMiddleware(middleware: FilteredMiddleware<T?, A>)

    fun addReducer(reducer: FilteredReducer<T, A>)

}

inline fun <T, reified A : Any> FilteredInitializer<T, A>.toInitializer(): Initializer<T> =
        object : Initializer<T> {

            private val initializer = this@toInitializer

            override fun addMiddleware(middleware: FilteredMiddleware<T, Any>) = initializer.addMiddleware(middleware)

            override fun addReducer(reducer: FilteredReducer<T, Any>) = initializer.addReducer(reducer)

        }

inline fun <T, reified A : Any> FilteredOptionalInitializer<T, A>.toOptionalInitializer(): OptionalInitializer<T> =
        object : OptionalInitializer<T> {

            private val initializer = this@toOptionalInitializer

            override fun addMiddleware(middleware: FilteredMiddleware<T?, Any>) = initializer.addMiddleware(middleware)

            override fun addReducer(reducer: FilteredReducer<T, Any>) = initializer.addReducer(reducer)

        }
