package io.lamart.kosmos

fun <T> ((T) -> Unit).toObserver() = Observer.from(this)

interface Observer<in T> : (T) -> Unit {

    companion object {

        fun <T> from(observer: (T) -> Unit): Observer<T> = object : Observer<T> {
            override fun invoke(state: T) = observer(state)
        }

        fun <T> wrap(vararg observers: (T) -> Unit): Observer<T> = wrap(observers.asIterable())

        fun <T> wrap(observers: Iterable<(T) -> Unit>): Observer<T> {
            var result = from { state: T -> }

            observers.forEach { next -> result = result.let { previous -> combine(previous, next) } }

            return result
        }

        fun <T> combine(
                previous: (T) -> Unit,
                next: (T) -> Unit
        ): Observer<T> = from { previous(it); next(it) }

    }

}