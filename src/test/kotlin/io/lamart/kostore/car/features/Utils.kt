package io.lamart.kostore.car.features

import io.lamart.kostore.Store
import io.lamart.kostore.car.*
import io.lamart.kostore.initializers.*

internal fun newStore(): Store<Car> = Store(Car()) {

    compose({ it.wheels }, { copy(wheels = it) })
            .filter<Map<Position, Wheel>, WheelAction.Change>()
            .composeFilteredMap({ it.position })
            .addReducer(wheelReducer)

    compose({ it.seats }, { copy(seats = it) })
            .filter<List<Seat>, SeatAction>()
            .composeFilteredList { seat, action -> seat.id == action.seatId }
            .addReducer(reducer)

    compose({ it.steer }, { copy(steer = it) })
            .compose({ it.buttons }, { copy(buttons = it) })
            .filter<Collection<Button>, ButtonAction>()
            .composeFilteredCollection({ state, action -> action.name == state.name })
            .addReducer(buttonReducer)

}
