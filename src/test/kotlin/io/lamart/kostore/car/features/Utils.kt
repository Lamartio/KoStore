package io.lamart.kostore.car.features

import io.lamart.kostore.Store
import io.lamart.kostore.car.*
import io.lamart.kostore.operators.*

internal fun newStore(): Store<Car> = Store(Car()) {

    compose({ wheels }, { copy(wheels = it) })
            .composeMap { position, action -> action is WheelAction.Change && position == action.position }
            .addReducer(wheelReducer)

    compose({ seats }, { copy(seats = it) })
            .composeList { seat, action -> action is SeatAction && seat.id == action.seatId }
            .addReducer(seatReducer)

    compose({ steer }, { copy(steer = it) })
            .compose({ buttons }, { copy(buttons = it) })
            .composeCollection({ state, action -> action is ButtonAction && state.name == action.name })
            .addReducer(buttonReducer)

}
