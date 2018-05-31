package io.lamart.kostore.car

import io.lamart.kostore.FilteredReducer

data class Seat(val id: Position, val seatBeltLocked: Boolean = false)
sealed class SeatAction(open val seatId: Position) {
    data class Lock(override val seatId: Position) : SeatAction(seatId)
    data class Unlock(override val seatId: Position) : SeatAction(seatId)
}

val reducer: FilteredReducer<Seat, SeatAction> = { seat, action ->
    when (action) {
        is SeatAction.Lock -> seat.copy(seatBeltLocked = true)
        is SeatAction.Unlock -> seat.copy(seatBeltLocked = true)
    }
}