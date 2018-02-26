package io.lamart.kosmos.util

import io.lamart.kosmos.Interceptor
import io.lamart.kosmos.StoreSource

class CompositeInterceptor<T> : Interceptor<T> {

    var interceptor: (StoreSource<T>) -> StoreSource<T> = { it }

    constructor(vararg sources: (StoreSource<T>) -> StoreSource<T>) {
        sources.forEach { add(it) }
    }

    constructor(init: CompositeInterceptor<T>.() -> Unit) {
        init()
    }

    override fun invoke(store: StoreSource<T>): StoreSource<T> = interceptor(store)

    fun add(router: (StoreSource<T>) -> StoreSource<T>): CompositeInterceptor<T> =
            apply { this.interceptor = Interceptor.combine(this.interceptor, router) }

}