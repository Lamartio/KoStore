package io.lamart.kosmos

interface StoreSource<out T> : (Any) -> Unit {

    val state: T

    operator fun invoke(): T = state

    fun dispatch(action: Any): StoreSource<T>

}