package io.lamart.kosmos

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserverTests {

    @Test
    fun test() {
        val list = mutableListOf<Int>()
        val observer: (Int) -> Unit = {
            assertEquals(0, it)
            list.add(3)
        }
        val observers = CompositeObservers<Int>()
                .add { list.add(1) }
                .add { list.add(2) }
                .add(observer)

        observers(0)
        assertArrayEquals(intArrayOf(1, 2, 3), list.toIntArray())
        list.clear()

        observers.remove(observer)
        observers(0)
        assertArrayEquals(intArrayOf(1, 2), list.toIntArray())
    }
}