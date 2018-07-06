package io.lamart.kostore.car

import io.lamart.kostore.FilteredReducer
import io.lamart.kostore.Reducer
import io.lamart.kostore.filter

data class Seat(val id: Position, val seatBeltLocked: Boolean = false)

sealed class SeatAction(open val seatId: Position) {
    data class Lock(override val seatId: Position) : SeatAction(seatId)
    data class Unlock(override val seatId: Position) : SeatAction(seatId)
}

val seatReducer: Reducer<Seat> = filter { seat, action: SeatAction ->
    when (action) {
        is SeatAction.Lock -> seat.copy(seatBeltLocked = true)
        is SeatAction.Unlock -> seat.copy(seatBeltLocked = true)
    }
}