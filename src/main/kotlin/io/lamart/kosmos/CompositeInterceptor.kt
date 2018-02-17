package io.lamart.kosmos

import io.lamart.kosmos.util.Interceptor

class CompositeInterceptor<T>(private val root: StoreSource<T>) : StoreSource<T> {

    var router: (StoreSource<T>) -> StoreSource<T> = { it }

    override val state: T = router(root).state

    override fun invoke(): T = router(root).invoke()

    override fun invoke(action: Any) = router(root).invoke(action)

    fun add(router: (StoreSource<T>) -> StoreSource<T>): CompositeInterceptor<T> =
            apply { this.router = Interceptor.combine(this.router, router) }

}