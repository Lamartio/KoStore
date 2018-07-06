package io.lamart.kostore.car

import io.lamart.kostore.FilteredReducer
import io.lamart.kostore.Reducer
import io.lamart.kostore.filter

val buttonReducer: Reducer<Button> = filter { button, action: ButtonAction ->
    when (action) {
        is ButtonAction.Press -> button.copy(name = button.name, pressed = true)
        is ButtonAction.UnPress -> button.copy(name = button.name, pressed = false)
    }
}

sealed class ButtonAction(open val name: String) {
    data class Press(override val name: String) : ButtonAction(name)
    data class UnPress(override val name: String) : ButtonAction(name)
}

data class Button(val name: String, val pressed: Boolean = false)
data class Steer(val horn: Horn = Horn(), val buttons: Collection<Button> = emptySet())
data class Horn(val isHonking: Boolean = false)