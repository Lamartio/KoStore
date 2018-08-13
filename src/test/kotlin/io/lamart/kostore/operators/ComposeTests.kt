package io.lamart.kostore.operators

import io.lamart.kostore.IntWrapper
import io.lamart.kostore.Store
import io.lamart.kostore.filter
import io.lamart.kostore.utility.toOptional
import org.junit.Assert.assertEquals
import org.junit.Test

class ComposeTests {

    @Test
    internal fun test() {
        var counter = 0
        val store = Store(IntWrapper(0)).apply {

            addReducer(filter { _, action: IntWrapper -> action })

            compose({ number }, { copy(number = it) }) {
                addMiddleware { getState, _, action, next ->
                    getState()
                    next(action)
                }
                addReducer { state: Int, _ ->
                    assertEquals(1, state)
                    counter++
                    state
                }
            }
        }

        store.dispatch(IntWrapper(1))
        assertEquals(1, counter)
    }

    @Test
    internal fun optionalTest() {
        var counter = 0
        val store = Store(IntWrapper(0)).apply {

            addReducer(filter { _, action: IntWrapper -> action })

            toOptional().compose({ number }, { copy(number = it) }) {
                addMiddleware { getState, _, action, next ->
                    getState()
                    next(action)
                }
                addReducer { state: Int, _ ->
                    assertEquals(1, state)
                    counter++
                    state
                }
            }
        }

        store.dispatch(IntWrapper(1))
        assertEquals(1, counter)
    }

}
