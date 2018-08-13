package io.lamart.kostore.operators

import io.lamart.kostore.OptionalInitializer
import io.lamart.kostore.Store
import io.lamart.kostore.utility.toOptional
import org.junit.Assert.*
import org.junit.Test

class ComposeMapTests {

    @Test
    fun test() {
        Store(mapOf(1 to "a", 2 to "b", 3 to "c")) {
            composeMap({ key, action -> key == action }, ::initialize)

            dispatch(2)
            assertEquals("B", getState()[2])
        }
    }

    @Test
    fun optionalTest() {
        Store(mapOf(1 to "a", 2 to "b", 3 to "c")) {
            toOptional().composeMap({ key, action -> key == action }, ::initialize)

            dispatch(2)
            assertEquals("B", getState()[2])
        }
    }

    private fun initialize(initializer: OptionalInitializer<String>) = with(initializer) {
        addMiddleware { getState, _, action, next ->
            getState()
            next(action)
        }

        addReducer { state: String, action: Any ->
            state.capitalize()
        }
    }

}