package io.lamart.kosmos

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
    fun combineReducers() =
            combine(reducer, reducer)
                    .run { invoke(0, "increment") }
                    .let { assertEquals(2, it) }

    @Test
    fun combineReducersVarargs() =
            combine(reducer, reducer, reducer)
                    .run { invoke(0, "increment") }
                    .let { assertEquals(3, it) }

    @Test
    fun combineReducersIterable() =
            listOf(reducer, reducer, reducer)
                    .let { combine(it) }
                    .run { invoke(0, "increment") }
                    .let { assertEquals(3, it) }

    @Test
    fun combineReducersIterator() =
            listOf(reducer, reducer, reducer)
                    .iterator()
                    .let { combine(it) }
                    .run { invoke(0, "increment") }
                    .let { assertEquals(3, it) }

    @Test
    fun typedReducer() {
        typed { state: Int, action: String -> reducer(state, action) }
                .invoke(0, "increment")
                .also { assertEquals(1, it) }
    }

}