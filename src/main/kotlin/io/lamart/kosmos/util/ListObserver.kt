package io.lamart.kosmos.util

import io.lamart.kosmos.Observer

open class ListObserver<T>(list: MutableList<Observer<T>> = ArrayList()) : MutableList<Observer<T>> by list, Observer<T> {

    override fun invoke(state: T) = forEach { it(state) }

}