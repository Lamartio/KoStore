package io.lamart.kostore.operators

import io.lamart.kostore.IntWrapper
import io.lamart.kostore.Store
import io.lamart.kostore.mathReducer
import io.lamart.kostore.shouldBe
import io.lamart.kostore.utility.toOptional
import org.junit.Test


data class Rubbish(val action: String)

class MapTests {

    @Test
    fun sendRubbish() {
        val store = Store(IntWrapper(0)) {
            map({ (it as? Rubbish)?.action ?: it }) {
                compose({ number }, { copy(number = it) }) {
                    addMiddleware { getState, dispatch, action, next ->
                        getState()
                        next(action)
                    }
                    addReducer(mathReducer)
                }
            }
        }

        store.run {
            getState().number shouldBe 0
            dispatch(Rubbish("increment"))
            getState().number shouldBe 1
            dispatch(Rubbish("decrement"))
            getState().number shouldBe 0
        }
    }

    @Test
    fun sendOptionalRubbish() {
        val store = Store(IntWrapper(0)) {
            toOptional().map({ (it as? Rubbish)?.action ?: it }) {
                compose({ number }, { copy(number = it) }) {
                    addMiddleware { getState, dispatch, action, next ->
                        getState()
                        next(action)
                    }
                    addReducer(mathReducer)
                }
            }
        }

        store.run {
            getState().number shouldBe 0
            dispatch(Rubbish("increment"))
            getState().number shouldBe 1
            dispatch(Rubbish("decrement"))
            getState().number shouldBe 0
        }
    }

}