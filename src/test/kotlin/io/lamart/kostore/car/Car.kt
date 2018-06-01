package io.lamart.kostore.car

data class Car(
        val wheels: Map<Position, Wheel> = mapOf(
                Position.FRONT_LEFT to Wheel(),
                Position.FRONT_RIGHT to Wheel(),
                Position.REAR_LEFT to Wheel(),
                Position.REAR_RIGHT to Wheel()
        ),
        val seats: List<Seat> = listOf(
                Seat(Position.FRONT_LEFT),
                Seat(Position.FRONT_RIGHT),
                Seat(Position.REAR_LEFT),
                Seat(Position.REAR_RIGHT)
        ),
        val steer: Steer = Steer(
                buttons = setOf(
                        Button("set cruise control"),
                        Button("stop cruise controler"),
                        Button("reset cruise control")
                )
        )
)

