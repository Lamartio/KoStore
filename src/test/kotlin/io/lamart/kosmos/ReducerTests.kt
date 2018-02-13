package io.lamart.kosmos

import org.junit.Assert.assertEquals
import org.junit.Test


class ReducerTests {

    @Test
    fun combineReducers() {
        var state = 1
        val reducer = CompositeReducer.combineReducers(Functions::emptyReducer, Functions::mathReducer)

        reducer(state, "increment")
                .also { state = it }
                .also { assertEquals(2, it) }

        reducer(state, "decrement")
                .also { state = it }
                .also { assertEquals(1, it) }
    }

    @Test
    fun multiCombineReducers() {
        var state = 1
        val reducer = CompositeReducer.combineReducers(
                Functions::emptyReducer,
                CompositeReducer.combineReducers(Functions::mathReducer, Functions::mathReducer)
        )

        reducer(state, "increment")
                .also { state = it }
                .also { assertEquals(3, it) }

        reducer(state, "decrement")
                .also { state = it }
                .also { assertEquals(1, it) }
    }

    @Test
    fun compositeReducer() {
        var state = 1
        val reducer = CompositeReducer(
                Functions::mathReducer,
                Functions::emptyReducer,
                Functions::mathReducer
        )

        reducer(state, "increment")
                .also { state = it }
                .also { assertEquals(3, it) }

        reducer(state, "decrement")
                .also { state = it }
                .also { assertEquals(1, it) }
    }

    @Test
    fun complexCompositeReducer() {
        var state = 1
        val reducer = CompositeReducer(
                Functions::mathReducer,
                Functions::mathReducer,
                CompositeReducer(Functions::emptyReducer, Functions::mathReducer)
        )

        reducer(state, "increment")
                .also { state = it }
                .also { assertEquals(4, it) }

        reducer(state, "decrement")
                .also { state = it }
                .also { assertEquals(1, it) }
    }

}