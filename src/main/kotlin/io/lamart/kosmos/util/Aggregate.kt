package io.lamart.kosmos.util

fun <T> aggregate(items: Array<T>, combiner: (T, T) -> T): T? = aggregate(items.iterator(), combiner)

fun <T> aggregate(items: Iterable<T>, combiner: (T, T) -> T): T? = aggregate(items.iterator(), combiner)

fun <T> aggregate(items: Iterator<T>, combiner: (T, T) -> T): T? =
        if (items.hasNext()) aggregate(items.next(), items, combiner) else null

fun <T> aggregate(initial: T, items: Array<T>, combiner: (T, T) -> T): T =
        aggregate(initial, items.iterator(), combiner)

fun <T> aggregate(initial: T, items: Iterable<T>, combiner: (T, T) -> T): T =
        aggregate(initial, items.iterator(), combiner)

fun <T> aggregate(initial: T, items: Iterator<T>, combiner: (T, T) -> T): T {
    var result = initial

    while (items.hasNext()) result = result.let { previous -> combiner(previous, items.next()) }

    return result
}