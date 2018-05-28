package io.lamart.kostore.initializers

import io.lamart.kostore.Initializer
import io.lamart.kostore.Middleware
import io.lamart.kostore.Reducer
import io.lamart.kostore.filter

inline fun <T, reified A> Initializer<T>.filter(): Initializer<T> =
        object : Initializer<T> {

            val initializer = this@filter

            override fun addMiddleware(middleware: Middleware<T>) {
                initializer.addMiddleware(filter<T, A>(middleware))
            }

            override fun addReducer(reducer: Reducer<T>) {
                initializer.addReducer(filter<T, A>(reducer))
            }

        }
