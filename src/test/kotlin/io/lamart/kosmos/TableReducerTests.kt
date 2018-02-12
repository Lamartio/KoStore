package io.lamart.kosmos

import io.lamart.kosmos.Store
import org.junit.Assert.assertEquals
import org.junit.Test

class TableReducerTests {

    data class Car(val steer: Steer, val gears: Gears)
    data class Steer(val horn: Horn, val rotation: Int)
    data class Gears(val current: Int)

    sealed class Horn {
        object Idle : Horn()
        object Honking : Horn()
    }

    val hornReducer = TableReducer<Horn> {
        typedState<Horn.Idle>().withTypedAction<Honk.Start>().creates { Horn.Honking }
        typedState<Horn.Honking>().withTypedAction<Honk.Stop>().creates { Horn.Honking }
    }

    val steerReducer = TableReducer<Steer> {
        anyState().withTypedAction<Turn>().creates { copy(rotation = getRotation(it)) }
        anyState().withAnyAction().creates { copy(horn = hornReducer(horn, it)) }
    }

    val gearsReducer = TableReducer<Gears> {
        state { current > -1 }.withTypedAction<Shift.Down>().creates { Gears(current - 1) }
        state { current < 6 }.withTypedAction<Shift.Up>().creates { Gears(current + 1) }
    }

    val carReducer = TableReducer<Car> {
        anyState().withAnyAction().creates { copy(steer = steerReducer(steer, it), gears = gearsReducer(gears, it)) }
    }

    @Test
    fun reducer() {
        val initialCar = Car(Steer(Horn.Idle, 0), Gears(0))

        carReducer(initialCar, Turn.Right(20))
                .apply { assertEquals(steer.rotation, 20) }

        carReducer(initialCar, Honk.Start)
                .apply { assertEquals(steer.horn, Horn.Honking) }
    }

    @Test
    fun reducerInStore() {
        Car(Steer(Horn.Idle, 0), Gears(0))
                .let (::Store)
                .addReducer(carReducer)
                .apply { dispatch(Honk.Start) }
                .apply { assertEquals(state.steer.horn, Horn.Honking) }
                .apply { dispatch(Shift.Up) }
                .apply { assertEquals(state.gears.current, 1) }
    }

    sealed class Turn {
        data class Left(val degrees: Int) : Turn()
        data class Right(val degrees: Int) : Turn()
    }

    sealed class Shift {
        object Up : Shift()
        object Down : Shift()
    }

    sealed class Honk {
        object Start : Honk()
        object Stop : Honk()
    }

    fun Steer.getRotation(turn: Turn): Int = when (turn) {

        is Turn.Left -> rotation - turn.degrees
        is Turn.Right -> rotation + turn.degrees

    }.let { Math.min(90, Math.max(-90, it)) }

}