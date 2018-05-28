package io.lamart.kostore

private sealed class MathAction {
    object Increment : MathAction()
    object Decrement : MathAction()
}

class InitializerTests {

    private val reducer: FilteredReducer<Int, MathAction> = { state: Int, action: MathAction ->
        when (action) {
            MathAction.Increment -> state + 1
            MathAction.Decrement -> state - 1
            else -> state
        }
    }

    private val flipMathMiddleware: FilteredMiddleware<Int, MathAction> = { _, _, action, next ->
        val newAction = when (action) {
            MathAction.Increment -> MathAction.Decrement
            MathAction.Decrement -> MathAction.Increment
            else -> action
        }

        next(newAction)
    }

    fun test() {
        Store(0) {

            filter<Int, MathAction> {
                addMiddleware(flipMathMiddleware)
                addReducer(reducer)
            }

        }
    }

}
