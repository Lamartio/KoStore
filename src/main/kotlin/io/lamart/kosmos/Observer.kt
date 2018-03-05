package io.lamart.kosmos

import io.lamart.kosmos.util.aggregate

typealias Observer<T> = (T) -> Unit

fun <T> combine(vararg items: Observer<T>): Observer<T> = aggregate(items, ::combine) ?: {}

fun <T> combine(items: Iterable<Observer<T>>): Observer<T> = aggregate(items, ::combine) ?: {}

fun <T> combine(items: Iterator<Observer<T>>): Observer<T> = aggregate(items, ::combine) ?: {}

fun <T> combine(previous: Observer<T>, next: Observer<T>): Observer<T> = { previous(it); next(it) }