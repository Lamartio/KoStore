package io.lamart.kosmos

import org.junit.Assert.assertEquals
import org.junit.Test

class StoreUtilTests {

    @Test
    fun reducer() {
        val reducer = Reducer.typed<Int, Int> { state, action -> state + action }

        assertEquals(2, reducer(1, 1))
        assertEquals(1, reducer(1, "1"))
    }


    @Test
    fun middleware() {
        val source = object : StoreSource<Int> {

            override var state: Int = 1

            override fun invoke(action: Any) {
                state += (action as Int)
            }

            override fun dispatch(action: Any): StoreSource<Int> = apply { invoke(action) }

        }
        val middleware = Middleware.typed<Int, Int> { store, action, next -> println(action) }
        Store(1) {

        }

        middleware(source, 1, { assertEquals(2, source.state) })
        middleware(source, "1", { assertEquals(1, source.state) })
    }

}
