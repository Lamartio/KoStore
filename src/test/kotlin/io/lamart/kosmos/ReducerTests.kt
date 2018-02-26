package io.lamart.kosmos

import io.lamart.kosmos.util.CompositeReducer
import org.junit.Assert.assertEquals
import org.junit.Test


class ReducerTests {

    @Test
    fun combineReducers() {
        val reducer = Reducer.combine(Functions::emptyReducer, Functions::mathReducer)

        reducer(1, "increment").also { assertEquals(2, it) }
        reducer(2, "decrement").also { assertEquals(1, it) }
    }

    @Test
    fun multiCombineReducers() {
        val reducer = Reducer.combine(
                Functions::emptyReducer,
                Reducer.combine(Functions::mathReducer, Functions::mathReducer)
        )

        reducer(1, "increment").also { assertEquals(3, it) }
        reducer(3, "decrement").also { assertEquals(1, it) }
    }

    @Test
    fun compositeReducer() {
        val reducer = CompositeReducer(
                Functions::mathReducer,
                Functions::emptyReducer,
                Functions::mathReducer
        )

        reducer(1, "increment").also { assertEquals(3, it) }
        reducer(3, "decrement").also { assertEquals(1, it) }
    }

    @Test
    fun complexCompositeReducer() {
        val reducer = CompositeReducer(
                Functions::mathReducer,
                Functions::mathReducer,
                CompositeReducer(Functions::emptyReducer, Functions::mathReducer)
        )

        reducer(1, "increment").also { assertEquals(4, it) }
        reducer(4, "decrement").also { assertEquals(1, it) }
    }

}