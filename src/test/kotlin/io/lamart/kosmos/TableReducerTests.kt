package io.lamart.kosmos

import io.lamart.kosmos.input.*
import org.junit.Assert.assertEquals
import org.junit.Test

class TableReducerTests {

    @Test
    fun reducer() {
        carReducer(Car(), Turn.Right(20)).apply { assertEquals(steer.rotation, 20) }
        carReducer(Car(), Honk.Start).apply { assertEquals(steer.horn, Horn.Honking) }
        carReducer(Car(), Shift.Up).apply { assertEquals(gears.current, 1) }
    }

    @Test
    fun reducerInStore() {
        Car().let { Store(it) { addReducer(carReducer) } }.apply {
            dispatch(Turn.Right(20))
            assertEquals(getState().steer.rotation, 20)

            dispatch(Honk.Start)
            assertEquals(getState().steer.horn, Horn.Honking)

            dispatch(Shift.Up)
            assertEquals(getState().gears.current, 1)
        }
    }

}