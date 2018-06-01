package io.lamart.kostore.car.features

import io.lamart.kostore.Feature
import io.lamart.kostore.Store
import io.lamart.kostore.car.Car
import io.lamart.kostore.car.Position
import io.lamart.kostore.car.Wheel
import io.lamart.kostore.car.WheelAction
import io.lamart.kostore.given
import io.lamart.kostore.shouldBe


private val Store<Car>.frontLeftWheel: Wheel
    get() = getState().wheels.entries.first { it.key == Position.FRONT_LEFT }.value

class ChangeWheelFeature : Feature({

    lateinit var store: Store<Car>

    given("the wheel is a 15 inch one") {

        setUp { store = newStore() }

        on("changing the wheel with a bigger one") {
            WheelAction.Change(Position.FRONT_LEFT, 17).let(store::dispatch)

            it("should have a wheel of 17 inch") { store.frontLeftWheel.size shouldBe 17 }
        }

        on("changing the wheel with a smaller one") {
            WheelAction.Change(Position.FRONT_LEFT, 13).let(store::dispatch)

            it("should have a wheel of 13 inch") { store.frontLeftWheel.size shouldBe 13 }
        }
    }

})