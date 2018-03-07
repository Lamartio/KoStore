package io.lamart.kosmos

import io.lamart.kosmos.input.IntWrapper
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreTests {

    private val mathReducer = { state: Int, action: Any ->
        when (action) {
            "increment" -> state + 1
            "decrement" -> state - 1
            else -> state
        }
    }

    private val flipMathMiddleware: Middleware<Int> = { _, _, action, next ->
        val nextAction = when (action) {
            "increment" -> "decrement"
            "decrement" -> "increment"
            else -> action
        }

        next(nextAction)
    }

    @Test
    fun reducer() {
        val store = Store(0).apply { addReducer(mathReducer) }

        store.dispatch("increment")
        assertEquals(1, store.getState())
    }

    @Test
    fun middlewareAndReducer() {
        val store = Store(0).apply {
            addMiddleware(flipMathMiddleware)
            addReducer(mathReducer)
        }

        store.dispatch("increment")
        assertEquals(-1, store.getState())
    }

    @Test
    fun compose() {
        val store = Store(IntWrapper()) {
            compose({ it.number }, { copy(number = it) }) {
                addMiddleware(flipMathMiddleware)
                addReducer(mathReducer)
            }
        }

        store.dispatch("decrement")
        assertEquals(1, store.getState().number)
    }

}
