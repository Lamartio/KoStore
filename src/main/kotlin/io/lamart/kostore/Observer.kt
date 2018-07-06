package io.lamart.kostore

typealias Observer<T> = (T) -> Unit

fun <T> combine(previous: Observer<T>, next: Observer<T>): Observer<T> = { previous(it); next(it) }
