package io.lamart.kostore.car

import io.lamart.kostore.Feature
import io.lamart.kostore.Store
import io.lamart.kostore.initializers.compose
import io.lamart.kostore.initializers.composeFilteredList
import io.lamart.kostore.initializers.composeFilteredMap
import io.lamart.kostore.initializers.filter
import io.lamart.kostore.shouldBe

class ChangeWheelFeature : Feature({

    val store = Store(Car()) {

        compose({ it.wheels }, { copy(wheels = it) })
                .filter<Map<Position, Wheel>, WheelAction.Change>()
                .composeFilteredMap({ it.position })
                .addReducer(wheelReducer)

        compose({ it.seats }, { copy(seats = it) })
                .filter<List<Seat>, SeatAction>()
                .composeFilteredList { seat, action -> seat.id == action.seatId }
                .addReducer(reducer)

    }

    given("the wheel is a 15 inch one") {

        setUp { store.dispatch(WheelAction.Change(Position.FRONT_LEFT, 15)) }

        on("changing the wheel with a bigger one") {
            WheelAction
                    .Change(Position.FRONT_LEFT, 17)
                    .let(store::dispatch)

            it("should have a wheel of 17 inch") { store.frontLeftWheel.size shouldBe 17 }
        }

        on("changing the wheel with a smaller one") {
            WheelAction
                    .Change(Position.FRONT_LEFT, 13)
                    .let(store::dispatch)

            it("should have a wheel of 13 inch") { store.frontLeftWheel.size shouldBe 13 }
        }
    }

})

private val Store<Car>.frontLeftWheel: Wheel
    get() = getState()
            .wheels
            .entries
            .first { it.key == Position.FRONT_LEFT }
            .value
