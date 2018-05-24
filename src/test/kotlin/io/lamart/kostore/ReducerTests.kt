package io.lamart.kostore

import org.junit.Assert.assertEquals
import org.junit.Test


class ReducerTests {

    private val reducer = { state: Int, action: Any ->
        when (action) {
            "increment" -> state + 1
            "decrement" -> state - 1
            else -> state
        }
    }

    @Test
    fun reducer() {
        assertEquals(1, reducer(0, "increment"))
        assertEquals(-1, reducer(0, "decrement"))
        assertEquals(0, reducer(0, "anything"))
    }

    @Test
    fun typedReducer() {
        filter { state: Int, action: String -> reducer(state, action) }
                .invoke(0, "increment")
                .also { assertEquals(1, it) }
    }

}