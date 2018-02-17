package io.lamart.kosmos

import io.lamart.kosmos.util.Middleware
import org.junit.Assert.assertEquals
import org.junit.Test

class MiddlewareTests {

    @Test
    fun combineMiddleware() {
        val store = Store(0)
        val middleware = Middleware.combine(
                Functions::flipMathMiddleware,
                Functions::flipMathMiddleware
        )

        middleware(store, "increment", { assertEquals("increment", it) })
    }

    @Test
    fun multiCombineMiddleware() {
        val store = Store(0)
        val middleware = Middleware.combine(
                Functions::flipMathMiddleware,
                Middleware.combine(
                        Functions::emptyMiddleware,
                        Functions::flipMathMiddleware
                )
        )

        middleware(store, "increment", { assertEquals("increment", it) })
    }

    @Test
    fun compositeMiddleware() {
        val store = Store(0)
        val middleware = CompositeMiddleware(
                Functions::flipMathMiddleware,
                Functions::flipMathMiddleware
        )

        middleware(store, "increment", { assertEquals("increment", it) })
    }

    @Test
    fun complexCompositeMiddleware() {
        val store = Store(0)
        val middleware = CompositeMiddleware(
                Functions::emptyMiddleware,
                Functions::emptyMiddleware,
                CompositeMiddleware(Functions::flipMathMiddleware, Functions::flipMathMiddleware)
        )

        middleware(store, "increment", { assertEquals("increment", it) })
    }
}