package io.lamart.kosmos

interface StoreSource<out T> {

    val state: T

    fun dispatch(action: Any): Unit

}