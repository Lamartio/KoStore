package io.lamart.kosmos

fun <T> ((StoreSource<T>) -> StoreSource<T>).toInterceptor(): Interceptor<T> = Interceptor.from(this)

interface Interceptor<T> : (StoreSource<T>) -> StoreSource<T> {

    companion object {

        fun <T> from(interceptor: (StoreSource<T>) -> StoreSource<T>): Interceptor<T> =
                object : Interceptor<T> {
                    override fun invoke(store: StoreSource<T>): StoreSource<T> = interceptor(store)
                }

        fun <T> wrap(vararg interceptors: (StoreSource<T>) -> StoreSource<T>): Interceptor<T> =
                wrap(interceptors.asIterable())

        fun <T> wrap(interceptors: Iterable<(StoreSource<T>) -> StoreSource<T>>): Interceptor<T> {
            var result = from<T> { it }

            interceptors.forEach { next -> result = result.let { previous -> combine(previous, next) } }

            return result
        }

        fun <T> combine(
                previous: (StoreSource<T>) -> StoreSource<T>,
                next: (StoreSource<T>) -> StoreSource<T>
        ): Interceptor<T> = from { it.let(previous).let(next) }

    }

}