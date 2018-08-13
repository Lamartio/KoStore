package io.lamart.kostore

import io.lamart.kostore.utility.ListObserver
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserverTests {

    /**
     * Whenever a action reaches the reducer, the store should emit it output. In this sendRubbish the action, does not hit a middleware or reducer so the resulting state is same as the initial state.
     */

    @Test
    fun addObserver() {
        Store(0).apply {
            addObserver(::observer)
            dispatch("")
            removeObserver(::observer)
        }
    }

    private fun observer(state: Int) = assertEquals(0, state)

    /**
     * This sendRubbish proves the working of a ListObserver. It is a List of observers that are called sequentially.
     */

    @Test
    fun listObserver() {
        var hits = 0
        ListObserver<Int>()
                .apply { add { hits++ } }
                .apply { add { hits++ } }
                .let { Store(0) { addObserver(it) } }
                .dispatch("")

        assertEquals(2, hits)
    }

    @Test
    fun combineObservers() {
        var hits = 0
        val observer: Observer<Int> = { hits++ }

        combine(observer, observer).invoke(0)
        assertEquals(2, hits)
    }

}