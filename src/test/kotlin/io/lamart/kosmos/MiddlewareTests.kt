package io.lamart.kosmos

import io.lamart.kosmos.input.IntWrapper
import io.lamart.kosmos.util.test
import org.junit.Assert.assertEquals
import org.junit.Test

class MiddlewareTests {

    private val state: Int get() = throw NotImplementedError("You should not use this")
    private fun dispatch(action: Any): Unit = throw NotImplementedError("You should not use this")

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
    fun beforeMiddleware() {
        val list = mutableListOf<String>()

        before<Int>({ _, _, _, _ -> list.add("before") })
                .invoke(::state, ::dispatch, "increment", { list.add("next") })

        assertEquals("before", list[0])
        assertEquals("next", list[1])
    }

    @Test
    fun afterMiddleware() {
        val list = mutableListOf<String>()

        after<Int>({ _, _, _, _ -> list.add("after") })
                .invoke(::state, ::dispatch, "increment", { list.add("next") })

        assertEquals("next", list[0])
        assertEquals("after", list[1])
    }

    @Test
    fun beforeAndAfterMiddleware() {
        val list = mutableListOf<String>()

        before<Int>({ _, _, _, _ -> list.add("before") })
                .after({ _, _, _, _ -> list.add("after") })
                .invoke(::state, ::dispatch, "increment", { list.add("next") })

        assertEquals("before", list[0])
        assertEquals("next", list[1])
        assertEquals("after", list[2])
    }

    @Test
    fun compose() {
        val wrapperMiddleware: Middleware<IntWrapper> = flipMathMiddleware.compose { it.number }

        wrapperMiddleware
                .test()
                .dispatch("increment")
                .invoke { assertEquals("decrement", it.first()) }
    }

}