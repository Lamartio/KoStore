package io.lamart.kostore.operators

import io.lamart.kostore.Store
import io.lamart.kostore.utility.toOptional
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test


class FilterTests {

    @Test
    fun test() {
        var counter = 0
        val store = Store<String?>(null).apply {

            addReducer { _, action ->
                when {
                    action is String && action == "state" -> action
                    else -> null
                }
            }

            filter({ it != null }) {
                addMiddleware { getState, dispatch, action, next ->
                    getState()
                    next(action)
                }
                addReducer { state: String?, _ ->
                    assertNotNull(state)
                    counter++
                    state
                }
            }
        }

        store.dispatch("state")
        store.dispatch("no-state")
        store.dispatch("state")
        assertEquals(2, counter)
    }

    @Test
    fun optionalTest() {
        var counter = 0
        val store = Store<String?>(null).apply {

            addReducer { _, action ->
                when {
                    action is String && action == "state" -> action
                    else -> null
                }
            }

            toOptional().filter({ it != null }) {
                addMiddleware { getState, dispatch, action, next ->
                    getState()
                    next(action)
                }
                addReducer { state: String?, _ ->
                    assertNotNull(state)
                    counter++
                    state
                }
            }
        }

        store.dispatch("state")
        store.dispatch("no-state")
        store.dispatch("state")
        assertEquals(2, counter)
    }

}