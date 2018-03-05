package io.lamart.kosmos

import io.lamart.kosmos.util.ListObserver
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserverTests {

    /**
     * Whenever a action reaches the reducer, the store should emit it output. In this test the action, does not hit a middleware or reducer so the resulting state is same as the initial state.
     */

    @Test
    fun addObserver() {
        Store(0)
                .addObserver { assertEquals(it, 0) }
                .apply { dispatch("") }
    }

    /**
     * This test proves the working of a ListObserver. It is a List of observers that are called sequentially.
     */

    @Test
    fun listObserver() {
        var hits = 0
        ListObserver<Int>()
                .apply { add { hits++ } }
                .apply { add { hits++ } }
                .let(Store(0)::addObserver)
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

    @Test
    fun combineObserversVarargs() {
        var hits = 0
        val observer: Observer<Int> = { hits++ }

        combine(observer, observer, observer).invoke(0)
        assertEquals(3, hits)
    }

    @Test
    fun combineObserversIterable() {
        var hits = 0
        val observer: Observer<Int> = { hits++ }

        combine(listOf(observer, observer, observer)).invoke(0)
        assertEquals(3, hits)
    }

    @Test
    fun combineObserversIterator() {
        var hits = 0
        val observer: Observer<Int> = { hits++ }

        combine(listOf(observer, observer, observer).iterator()).invoke(0)
        assertEquals(3, hits)
    }

}