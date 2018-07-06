package io.lamart.kostore

import io.lamart.kostore.composition.compose
import io.lamart.kostore.input.IntWrapper
import org.junit.Assert.assertEquals
import org.junit.Test


class StoreTests {

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
            compose({ number }, { copy(number = it) }) {
                addMiddleware(flipMathMiddleware)
                addReducer(mathReducer)
            }
        }

        store.dispatch("decrement")
        assertEquals(1, store.getState().number)
    }

}
