package io.lamart.kostore

interface StoreSource<out T> {

    fun getState(): T

    infix fun dispatch(action: Any)

    infix fun addObserver(observer: Observer<T>)

    operator fun Observer<T>.unaryPlus() = addObserver(this)

}