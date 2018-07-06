package io.lamart.kostore

import io.lamart.kostore.composition.compose
import io.lamart.kostore.composition.mapAction
import io.lamart.kostore.input.IntWrapper
import org.junit.Test


data class Rubbish(val action: String)

class MapActionTest {

    @Test
    fun sendRubbish() {
        val store = Store(IntWrapper(0)) {
            mapAction { (it as? Rubbish)?.action ?: it }
                    .compose({ number }, { copy(number = it) })
                    .addReducer(mathReducer)
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