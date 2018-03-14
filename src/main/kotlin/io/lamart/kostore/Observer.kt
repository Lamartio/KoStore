package io.lamart.kostore

typealias Observer<T> = (T) -> Unit

fun <T> observer(): Observer<T> = {}

fun <T> combine(previous: Observer<T>, next: Observer<T>): Observer<T> = { previous(it); next(it) }

fun <I, O> Observer<O>.compose(get: (I) -> O): Observer<I> = { get(it).let(::invoke) }