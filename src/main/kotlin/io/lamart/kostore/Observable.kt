package io.lamart.kostore

interface Observable<T> {

    fun addObserver(observer: Observer<T>)

    fun removeObserver(observer: Observer<T>)

}