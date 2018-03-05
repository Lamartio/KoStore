package io.lamart.kosmos

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
        Car().let(::Store)
                .addReducer(carReducer)
                .apply { dispatch(Turn.Right(20)) }
                .apply { assertEquals(state.steer.rotation, 20) }
                .apply { dispatch(Honk.Start) }
                .apply { assertEquals(state.steer.horn, Horn.Honking) }
                .apply { dispatch(Shift.Up) }
                .apply { assertEquals(state.gears.current, 1) }
    }

}