package io.lamart.kostore.car.features

import io.lamart.kostore.Feature
import io.lamart.kostore.Store
import io.lamart.kostore.car.Car
import io.lamart.kostore.car.Position
import io.lamart.kostore.car.Seat
import io.lamart.kostore.car.SeatAction
import io.lamart.kostore.given
import io.lamart.kostore.shouldBe

private val Store<Car>.driverSeat: Seat
    get() = getState().seats.first { it.id == Position.FRONT_LEFT }

class LockSeatbeltFeature : Feature({

    lateinit var store: Store<Car>

    given("the seatbelt of the driver is unlocked") {

        setUp { store = newStore() }

        on("locking the seatbelt") {
            SeatAction.Lock(Position.FRONT_LEFT).let(store::dispatch)

            it("should be locked") { store.driverSeat.seatBeltLocked shouldBe true }
        }

    }
})