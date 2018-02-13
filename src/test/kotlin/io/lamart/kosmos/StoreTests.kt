package io.lamart.kosmos

import org.junit.Assert.assertEquals
import org.junit.Test

class StoreTests {

    @Test
    fun reducer() {
        val store = Store(0).addReducer(Functions::mathReducer)

        store.dispatch("increment")
        store.dispatch("increment")
        store.dispatch("increment")

        assertEquals(3, store.state)
    }

    @Test
    fun reducerAndMiddleware1() {
        val store = Store(0)
                .addMiddleware(Functions::flipMathMiddleware)
                .addReducer(Functions::mathReducer)

        store.dispatch("increment")
        store.dispatch("increment")

        assertEquals(-2, store.state)
    }

    @Test
    fun reducerAndMiddleware2() {
        val store = Store(0)
                .addMiddleware(Functions::multiEmitMiddleware)
                .addMiddleware(Functions::flipMathMiddleware)
                .addReducer(Functions::mathReducer)

        store.dispatch("increment")

        assertEquals(-2, store.state)
    }

    @Test
    fun reducerAndMiddleware3() {
        var received = false
        val store = Store(0)
                .addMiddleware(Functions::logMiddleware)
                .addMiddleware(Functions::flipMathMiddleware)
                .addReducer(Functions::mathReducer)
                .addObserver { received = true }

        store.dispatch("increment")

        assertEquals(-1, store.state)
        assertEquals(true, received)
    }

}
