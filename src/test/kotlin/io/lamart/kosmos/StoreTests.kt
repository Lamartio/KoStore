package io.lamart.kosmos

import org.junit.Assert.assertEquals
import org.junit.Test

class StoreTests {

    private val reducer = { state: Int, action: Any ->
        when (action) {
            "increment" -> state + 1
            "decrement" -> state - 1
            else -> state
        }
    }

    private val middleware: Middleware<Int> = { store, action, next ->
        val nextAction = when (action) {
            "increment" -> "decrement"
            "decrement" -> "increment"
            else -> action
        }

        next(nextAction)
    }

    @Test
    fun reducer() {
        val store = Store(0) { reducer += this@StoreTests.reducer }

        store.dispatch("increment")
        assertEquals(1, store.state)
    }

    @Test
    fun middlewareAndReducer() {
        val store = Store(0)
                .addMiddleware(middleware)
                .addReducer(reducer)

        store.dispatch("increment")
        assertEquals(-1, store.state)
    }

}
