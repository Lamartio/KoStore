package io.lamart.kosmos

interface StoreSource<out T> {

    fun getState(): T

    infix fun dispatch(action: Any)

    infix fun addObserver(observer: Observer<T>)

}