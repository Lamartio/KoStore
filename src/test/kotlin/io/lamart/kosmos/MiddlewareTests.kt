package io.lamart.kosmos

import io.lamart.kosmos.util.test
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class MiddlewareTests {

    private val store = object : StoreSource<Int> {
        override val state: Int get() = throw NotImplementedError("You should not use this")
        override fun dispatch(action: Any) = throw NotImplementedError("You should not use this")
    }

    private val flipMathMiddleware: Middleware<Int> = { store, action, next ->
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
                .dispatch("increment")
                .invoke { assertEquals("decrement", it.first()) }
    }

    @Test
    fun combineMiddleware() {
        combine(flipMathMiddleware, flipMathMiddleware)
                .test()
                .dispatch("increment")
                .invoke { assertEquals("increment", it.first()) }
    }

    @Test
    fun combineMiddlewareVarargs() {
        combine(flipMathMiddleware, flipMathMiddleware, flipMathMiddleware)
                .test()
                .dispatch("increment")
                .invoke { assertEquals("decrement", it.first()) }
    }

    @Test
    fun combineMiddlewareIterable() {
        listOf(flipMathMiddleware, flipMathMiddleware, flipMathMiddleware)
                .let { combine(it) }
                .test()
                .dispatch("increment")
                .invoke { assertEquals("decrement", it.first()) }
    }

    @Test
    fun combineMiddlewareIterator() {
        listOf(flipMathMiddleware, flipMathMiddleware, flipMathMiddleware)
                .iterator()
                .let { combine(it) }
                .test()
                .dispatch("increment")
                .invoke { assertEquals("decrement", it.first()) }
    }

    @Test
    fun typedMiddleware() {
        val state = AtomicInteger(0)
        val middleware = { store: StoreSource<AtomicInteger>, action: String, next: (Any) -> Unit ->
            store.state.incrementAndGet()
            next(action)
        }

        typed(middleware)
                .test(state)
                .dispatch(0)
                .dispatch("increment")
                .invoke { assertEquals(1, state.get()) }
    }

    @Test
    fun beforeMiddleware() {
        val list = mutableListOf<String>()

        before<Int>({ store, action, next -> list.add("before") })
                .invoke(store, "increment", { list.add("next") })

        assertEquals("before", list[0])
        assertEquals("next", list[1])
    }

    @Test
    fun afterMiddleware() {
        val list = mutableListOf<String>()

        after<Int>({ store, action, next -> list.add("after") })
                .invoke(store, "increment", { list.add("next") })

        assertEquals("next", list[0])
        assertEquals("after", list[1])
    }

    @Test
    fun beforeAndAfterMiddleware() {
        val list = mutableListOf<String>()

        before<Int>({ store, action, next -> list.add("before") })
                .after({ store, action, next -> list.add("after") })
                .invoke(store, "increment", { list.add("next") })

        assertEquals("before", list[0])
        assertEquals("next", list[1])
        assertEquals("after", list[2])
    }

}