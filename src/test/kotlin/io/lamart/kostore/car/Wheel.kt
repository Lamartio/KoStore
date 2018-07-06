package io.lamart.kostore.car

import io.lamart.kostore.FilteredReducer
import io.lamart.kostore.Reducer
import io.lamart.kostore.filter

data class Wheel(val size: Int = 15)
sealed class WheelAction {
    data class Change(val position: Position, val size: Int) : WheelAction()
}

val wheelReducer: Reducer<Wheel> = filter { wheel, action: WheelAction ->
    when (action) {
        is WheelAction.Change -> wheel.copy(size = action.size)
    }
}

