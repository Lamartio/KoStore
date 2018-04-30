package io.lamart.kostore

import io.lamart.kostore.utils.aggregate
import org.junit.Assert.assertEquals
import org.junit.Test

class AggregateTests {

    @Test
    fun aggregateOptionalIterator() {
        val items = listOf(1, 2, 3).iterator()
        val aggregate = aggregate(items, { previous, next -> previous + next })

        assertEquals(6, aggregate)
    }

    @Test
    fun aggregateIterator() {
        val items = listOf(2, 3).iterator()
        val aggregate = aggregate(1, items, { previous, next -> previous + next })

        assertEquals(6, aggregate)
    }

}