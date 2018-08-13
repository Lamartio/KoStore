package io.lamart.kostore.operators

import io.lamart.kostore.*
import io.lamart.kostore.utility.toOptional
import org.junit.Test

private data class Action(val current: Int, val next: Int)

class ComposeCollectionTests {

    @Test
    fun collectionTest() {
        Store(setOf(1, 2, 3) as Collection<Int>) {
            composeCollection(
                    { state, action -> action is Action && action.current == state },
                    ::initialize
            )

            dispatch(Action(1, 0))
            getState() shouldBe setOf(0, 2, 3)
        }
    }

    @Test
    fun optionalCollectionTest() {
        Store(setOf(1, 2, 3) as Collection<Int>) {
            toOptional().composeCollection(
                    { state, action -> action is Action && action.current == state },
                    ::initialize
            )

            dispatch(Action(1, 0))
            getState() shouldBe setOf(0, 2, 3)
        }
    }

    @Test
    fun setTest() {
        Store(setOf(1, 2, 3)) {
            composeSet(
                    { state, action -> action is Action && action.current == state },
                    ::initialize
            )

            dispatch(Action(1, 0))
            getState() shouldBe setOf(0, 2, 3)
        }
    }

    @Test
    fun optionalSetTest() {
        Store(setOf(1, 2, 3)) {
            toOptional().composeSet(
                    { state, action -> action is Action && action.current == state },
                    ::initialize
            )

            dispatch(Action(1, 0))
            getState() shouldBe setOf(0, 2, 3)
        }
    }

    @Test
    fun listTest() {
        Store(listOf(1, 2, 3)) {
            composeList(
                    { state, action -> action is Action && action.current == state },
                    ::initialize
            )

            dispatch(Action(1, 0))
            getState() shouldBe listOf(0, 2, 3)
        }
    }

    @Test
    fun optionalListTest() {
        Store(listOf(1, 2, 3)) {
            toOptional().composeList(
                    { state, action -> action is Action && action.current == state },
                    ::initialize
            )

            dispatch(Action(1, 0))
            getState() shouldBe listOf(0, 2, 3)
        }
    }

    private fun initialize(initializer: OptionalInitializer<Int>) =
            with(initializer) {

                addMiddleware { getState, _, action, next ->
                    getState()
                    next(action)
                }

                addReducer { state, action ->
                    when (action) {
                        is Action -> action.next
                        else -> state
                    }
                }

            }

}