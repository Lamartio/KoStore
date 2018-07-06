package io.lamart.kostore

val flipMathMiddleware: Middleware<Int> = { _, _, action, next ->
    val nextAction = when (action) {
        "increment" -> "decrement"
        "decrement" -> "increment"
        else -> action
    }

    next(nextAction)
}