package io.lamart.kostore.utils

import io.lamart.kostore.Observer

open class ListObserver<T>(list: MutableList<Observer<T>> = mutableListOf()) : MutableList<Observer<T>> by list, Observer<T> {

    override fun invoke(state: T) = forEach { observer -> observer(state) }

}