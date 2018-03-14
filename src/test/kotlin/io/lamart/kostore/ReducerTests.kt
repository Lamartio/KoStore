package io.lamart.kostore

import io.lamart.kostore.input.IntWrapper
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
        reducer(0, "increment") == 1
        reducer(0, "decrement") == -1
        reducer(0, "anything") == 0
    }

    @Test
    fun typedReducer() {
        filter { state: Int, action: String -> reducer(state, action) }
                .invoke(0, "increment")
                .also { assertEquals(1, it) }
    }

    @Test
    fun compose() {
        val wrapperReducer: Reducer<IntWrapper> = reducer.compose({ it.number }, { copy(number = it) })
        val value = wrapperReducer(IntWrapper(0), "increment")

        assertEquals(1, value.number)
    }
}