package io.lamart.kostore.utility

import io.lamart.kostore.Observer

open class ListObserver<T>(list: MutableList<Observer<T>> = mutableListOf()) : MutableList<Observer<T>> by list, Observer<T> {

    override fun invoke(state: T) = toList().forEach { observer -> observer(state) }

}