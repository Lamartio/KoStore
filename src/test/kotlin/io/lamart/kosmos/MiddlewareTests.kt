package io.lamart.kosmos

import io.lamart.kosmos.util.CompositeMiddleware
import io.lamart.kosmos.util.test
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class MiddlewareTests {

    @Test
    fun middlewareTester() {
        Middleware
                .from<String> { store, action, next -> next(action) }
                .test()
                .dispatch(1)
                .dispatch(2)
                .dispatch(3)
                .invoke { assertArrayEquals(arrayOf(1, 2, 3), it.toTypedArray()) }
    }

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

    @Test
    fun beforeAndAfter() {
        var isNextCalled = false
        val store = Store(0) {
            addMiddleware { store, action, next -> isNextCalled = true; next(action) }
            addMiddleware { store, action, next ->
                Middleware
                        .before<String> { store -> assertEquals(false, isNextCalled) }
                        .after { store -> assertEquals(true, isNextCalled) }
            }
        }

        store.dispatch("")
    }

}