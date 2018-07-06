package io.lamart.kostore.input

import io.lamart.kostore.utility.TableReducer
import io.lamart.kostore.utility.creates


internal data class Car(val steer: Steer, val gears: Gears) {
    constructor() : this(Steer(), Gears())
}

internal data class Steer(val horn: Horn, val rotation: Int) {
    constructor() : this(Horn.Idle, 0)
}

internal data class Gears(val current: Int) {
    constructor() : this(0)
}

internal sealed class Horn {
    object Idle : Horn()
    object Honking : Horn()
}

internal sealed class Turn {
    data class Left(val degrees: Int) : Turn()
    data class Right(val degrees: Int) : Turn()
}

internal sealed class Shift {
    object Up : Shift()
    object Down : Shift()
}

internal sealed class Honk {
    object Start : Honk()
    object Stop : Honk()
}

internal val carReducer = TableReducer<Car> {
    anyState().withAnyAction().creates { copy(steer = steerReducer(steer, it), gears = gearsReducer(gears, it)) }
}

internal val steerReducer = TableReducer<Steer> {
    anyState().apply {
        withAction<Turn>().creates { copy(rotation = getRotation(it)) }
        withAnyAction().creates { copy(horn = hornReducer(horn, it)) }
    }
}

internal val hornReducer = TableReducer<Horn> {
    state<Horn.Idle>().withAction<Honk.Start>().creates { Horn.Honking }
    state<Horn.Honking>().withAction<Honk.Stop>().creates { Horn.Idle }
}

internal val gearsReducer = TableReducer<Gears> {
    state { current > -1 }.withAction<Shift.Down>().creates { Gears(current - 1) }
    state { current < 6 }.withAction<Shift.Up>().creates { Gears(current + 1) }
}

internal fun Steer.getRotation(turn: Turn): Int = when (turn) {

    is Turn.Left -> rotation - turn.degrees
    is Turn.Right -> rotation + turn.degrees

}.let { Math.min(90, Math.max(-90, it)) }