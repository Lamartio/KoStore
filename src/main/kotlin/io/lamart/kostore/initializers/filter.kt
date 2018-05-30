package io.lamart.kostore.initializers

import io.lamart.kostore.*

inline fun <T, reified A : Any> Initializer<T>.filter(): FilteredInitializer<T, A> =
        object : FilteredInitializer<T, A> {

            val initializer = this@filter

            override fun addMiddleware(middleware: FilteredMiddleware<T, A>) {
                initializer.addMiddleware(filter(middleware))
            }

            override fun addReducer(reducer: FilteredReducer<T, A>) {
                initializer.addReducer(filter(reducer))
            }

        }

inline fun <T, reified A : Any> OptionalInitializer<T>.filter(): FilteredOptionalInitializer<T, A> =
        object : FilteredOptionalInitializer<T, A> {

            val initializer = this@filter

            override fun addMiddleware(middleware: FilteredMiddleware<T?, A>) {
                initializer.addMiddleware(filter(middleware))
            }

            override fun addReducer(reducer: FilteredReducer<T, A>) {
                initializer.addReducer(filter(reducer))
            }

        }
