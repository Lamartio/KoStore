package io.lamart.kostore.operators

import io.lamart.kostore.Store
import io.lamart.kostore.filter
import io.lamart.kostore.utility.toOptional
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CastTests {

    @Test
    internal fun test() {
        var counter = 0
        val store = Store("test").apply {

            addReducer(filter { _, action: String -> action })

            cast<CharSequence> {

                addMiddleware { getState, dispatch, action, next ->
                    getState()
                    next(action)
                }

                addReducer { state: CharSequence, _ ->
                    assertEquals("123", state)
                    counter++
                    state
                }
            }
        }

        store.dispatch("123")
        assertEquals(1, counter)
    }

    @Test
    internal fun optionalTest() {
        var counter = 0
        val store = Store("test").apply {

            addReducer(filter { _, action: String -> action })

            toOptional().cast<CharSequence> {

                addMiddleware { getState, dispatch, action, next ->
                    getState()
                    next(action)
                }

                addReducer { state: CharSequence, _ ->
                    assertEquals("123", state)
                    counter++
                    state
                }
            }
        }

        store.dispatch("123")
        assertEquals(1, counter)
    }

}
