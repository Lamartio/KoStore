package io.lamart.kosmos

import io.lamart.kosmos.util.aggregate
import org.junit.Assert.assertEquals
import org.junit.Test

class AggregateTests {

    @Test
    fun aggregateEmpty() {
        val items = arrayOf<Int>()
        val aggregate = aggregate(items, { previous, next -> previous + next })

        assertEquals(null, aggregate)
    }

    @Test
    fun aggregateOptionalArray() {
        val items = arrayOf(1, 2, 3)
        val aggregate = aggregate(items, { previous, next -> previous + next })

        assertEquals(6, aggregate)
    }

    @Test
    fun aggregateOptionalList() {
        val items = listOf(1, 2, 3)
        val aggregate = aggregate(items, { previous, next -> previous + next })

        assertEquals(6, aggregate)
    }

    @Test
    fun aggregateOptionalIterator() {
        val items = listOf(1, 2, 3).iterator()
        val aggregate = aggregate(items, { previous, next -> previous + next })

        assertEquals(6, aggregate)
    }

    @Test
    fun aggregateList() {
        val items = listOf(2, 3)
        val aggregate = aggregate(1, items, { previous, next -> previous + next })

        assertEquals(6, aggregate)
    }

    @Test
    fun aggregateIterator() {
        val items = listOf(2, 3).iterator()
        val aggregate = aggregate(1, items, { previous, next -> previous + next })

        assertEquals(6, aggregate)
    }

}