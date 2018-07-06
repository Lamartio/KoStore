package io.lamart.kostore


interface StoreSource<T> : Observable<T> {

    fun getState(): T

    fun dispatch(action: Any)

}