package io.lamart.kostore.car

import io.lamart.kostore.FilteredReducer

data class Wheel(val size: Int = 15)
sealed class WheelAction {
    data class Change(val position: Position, val size: Int) : WheelAction()
}

val wheelReducer: FilteredReducer<Wheel, WheelAction> = { wheel, action ->
    when (action) {
        is WheelAction.Change -> wheel.copy(size = action.size)
    }
}

