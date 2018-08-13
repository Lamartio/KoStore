package io.lamart.kostore

import io.lamart.kostore.operators.CastInitializer
import io.lamart.kostore.operators.ComposeInitializer
import io.lamart.kostore.operators.FilterInitializer
import io.lamart.kostore.operators.MapInitializer


interface Initializer<T> {

    fun addMiddleware(middleware: Middleware<T>)

    fun addReducer(reducer: Reducer<T>)

    fun <R> cast(block: Initializer<R>.() -> Unit) = cast<R>().run(block)

    fun <R> cast(): Initializer<R> = CastInitializer(this)

    fun <R> compose(
            map: T.() -> R,
            reduce: T.(R) -> T,
            block: Initializer<R>.() -> Unit
    ) = compose(map, reduce).run(block)

    fun <R> compose(
            map: T.() -> R,
            reduce: T.(R) -> T
    ): Initializer<R> = ComposeInitializer(this, map, reduce)

    fun filter(predicate: T.(T) -> Boolean, block: OptionalInitializer<T>.() -> Unit) =
            filter(predicate).run(block)

    fun filter(predicate: T.(T) -> Boolean): OptionalInitializer<T> =
            FilterInitializer(this, predicate)

    fun map(map: (Any) -> Any, block: Initializer<T>.() -> Unit) =
            this.map(map).run(block)

    fun map(mapAction: (Any) -> Any): Initializer<T> =
            MapInitializer(this, mapAction)

}