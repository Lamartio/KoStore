package io.lamart.kosmos

import org.junit.Assert.assertEquals
import org.junit.Test

class MiddlewareTests {

    @Test
    fun combineMiddleware() {
        val store = Store(0)
        val middleware = CompositeMiddleware.combineMiddlewares(
                Functions::flipMathMiddleware,
                Functions::flipMathMiddleware
        )

        middleware(store, "increment", { assertEquals("increment", it) })
    }

    @Test
    fun multiCombineMiddleware() {
        val store = Store(0)
        val middleware = CompositeMiddleware.combineMiddlewares(
                Functions::flipMathMiddleware,
                CompositeMiddleware.combineMiddlewares(
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