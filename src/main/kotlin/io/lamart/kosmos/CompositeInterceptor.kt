package io.lamart.kosmos

import io.lamart.kosmos.util.Interceptor

class CompositeInterceptor<T>(private val root: StoreSource<T>) : StoreSource<T> {

    var interceptor: (StoreSource<T>) -> StoreSource<T> = { it }

    override val state: T = interceptor(root).state

    override fun invoke(): T = interceptor(root).invoke()

    override fun invoke(action: Any) = interceptor(root).invoke(action)

    fun add(router: (StoreSource<T>) -> StoreSource<T>): CompositeInterceptor<T> =
            apply { this.interceptor = Interceptor.combine(this.interceptor, router) }

}