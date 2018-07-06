package io.lamart.kostore

import io.lamart.kostore.utility.test
import org.junit.Assert.assertEquals
import org.junit.Test

class MiddlewareTests {

    private val flipMathMiddleware: Middleware<Int> = { _, _, action, next ->
        val newAction = when (action) {
            "increment" -> "decrement"
            "decrement" -> "increment"
            else -> action
        }

        next(newAction)
    }

    @Test
    fun middlewareTester() {
        flipMathMiddleware
                .test()
                .invoke("increment")
                .run { nexts }
                .also { assertEquals("decrement", it.first()) }
    }

    @Test
    fun combineMiddleware() {
        combine(flipMathMiddleware, flipMathMiddleware)
                .test()
                .invoke("increment")
                .run { nexts }
                .also { assertEquals("increment", it.first()) }
    }

}