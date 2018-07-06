package io.lamart.kostore

val mathReducer = { state: Int, action: Any ->
    when (action) {
        "increment" -> state + 1
        "decrement" -> state - 1
        else -> state
    }
}