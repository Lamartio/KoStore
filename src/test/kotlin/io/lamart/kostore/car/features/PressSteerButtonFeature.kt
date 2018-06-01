package io.lamart.kostore.car.features

import io.lamart.kostore.Feature
import io.lamart.kostore.Store
import io.lamart.kostore.car.Button
import io.lamart.kostore.car.ButtonAction
import io.lamart.kostore.car.Car
import io.lamart.kostore.given
import io.lamart.kostore.shouldBe


private val Store<Car>.cruiseControlButton: Button
    get() = getState().steer.buttons.first { it.name == "set cruise control" }

class PressSteerButtonFeature : Feature({

    val store: Store<Car> = newStore()

    given("a steer with cruise control buttons") {

        on("pressing the 'set cruise control button'") {
            ButtonAction.Press("set cruise control").let(store::dispatch)
            it("should become pressed") { store.cruiseControlButton.pressed shouldBe true }
        }

        on("unpressing the 'set cruise control button'") {
            ButtonAction.UnPress("set cruise control").let(store::dispatch)
            it("should become unpressed") { store.cruiseControlButton.pressed shouldBe false }
        }
    }

})
