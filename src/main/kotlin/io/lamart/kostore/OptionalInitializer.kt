package io.lamart.kostore

import io.lamart.kostore.operators.CastOptionalInitializer
import io.lamart.kostore.operators.ComposeOptionalInitializer
import io.lamart.kostore.operators.FilterOptionalInitializer
import io.lamart.kostore.operators.MapOptionalInitializer


interface OptionalInitializer<T> {

    fun addMiddleware(middleware: Middleware<T?>)

    fun addReducer(reducer: Reducer<T>)

    fun <R> cast(block: OptionalInitializer<R>.() -> Unit) = cast<R>().run(block)

    fun <R> cast(): OptionalInitializer<R> = CastOptionalInitializer(this)

    fun <R> compose(
            map: T.() -> R,
            reduce: T.(R) -> T,
            block: OptionalInitializer<R>.() -> Unit
    ) = compose(map, reduce).run(block)

    fun <R> compose(
            map: T.() -> R,
            reduce: T.(R) -> T
    ): OptionalInitializer<R> = ComposeOptionalInitializer(this, map, reduce)

    fun filter(predicate: T.(T) -> Boolean, block: OptionalInitializer<T>.() -> Unit) =
            filter(predicate).run(block)

    fun filter(predicate: T.(T) -> Boolean) =
            FilterOptionalInitializer(this, predicate)

    fun map(mapAction: (Any) -> Any, block: OptionalInitializer<T>.() -> Unit) =
            this.map(mapAction).run(block)

    fun map(mapAction: (Any) -> Any): OptionalInitializer<T> =
            MapOptionalInitializer(this, mapAction)

}