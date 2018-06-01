package io.lamart.kostore

import org.hamcrest.Matcher
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

@Ignore
open class Feature(private val block: Feature.() -> Unit = {}) : () -> Unit {

    @Test
    override fun invoke() {
        block()
    }

}

infix fun <T> T.shouldBe(expected: T) = Assert.assertEquals(expected, this)
infix fun <T> T.shouldBe(expected: Matcher<T>) = Assert.assertThat(this, expected)

fun given(text: String, block: Given.() -> Unit) {
    Given(text, block)
}

class Given internal constructor(
        text: String,
        block: Given.() -> Unit = {}
) : FeatureComponent("\n\nGiven $text") {

    private var setUp: Given.() -> Unit = {}
    private var tearDown: Given.() -> Unit = {}

    init {
        block()
    }

    fun setUp(block: Given.() -> Unit) {
        setUp = setUp.let { previous -> { previous(); block() } }
    }

    fun tearDown(block: Given.() -> Unit) {
        tearDown = tearDown.let { previous -> { previous(); block() } }
    }

    fun on(text: String, block: On.() -> Unit = {}): On =
            On(this, setUp, tearDown, text, block).also { conditions.add(it) }

    override fun toString(): String = "$text\n\n${super.toString()}"

}

class On internal constructor(
        private val given: Given,
        setUp: Given.() -> Unit = {},
        tearDown: Given.() -> Unit = {},
        text: String,
        block: On.() -> Unit = {}
) : FeatureComponent(text = "On $text") {

    init {
        given.run {
            setUp()
            block()
            tearDown()
        }
    }

    infix fun it(text: String) {

    }

    fun it(text: String, block: It.() -> Unit = {}): It =
            It(this, text, block).also { conditions.add(it) }

    override fun toString(): String = "$given\n$text\n${super.toString()}"

}

class It internal constructor(
        private val on: On,
        text: String,
        block: It.() -> Unit = {}
) : FeatureComponent(text = "It $text") {

    init {
        try {
            block()
        } catch (error: AssertionError) {
            throw AssertionError(toString(), error)
        }
    }

    override fun toString(): String = "$on\n$text\n${super.toString()}"

}

open class FeatureComponent internal constructor(
        protected val text: String,
        private val depth: Int = 0
) {

    protected val conditions: MutableList<FeatureComponent> = mutableListOf()

    fun and(text: String): FeatureComponent =
            apply { conditions.add(FeatureComponent("And $text", depth + 1)) }

    fun or(text: String): FeatureComponent =
            apply { conditions.add(FeatureComponent("Or $text", depth + 1)) }

    override fun toString(): String =
            flattenConditions()
                    .toMutableList()
                    .also { it.add(this) }
                    .joinToString(
                            separator = "\n",
                            transform = { getIndent(it.depth) + it.text }
                    )

    private fun getIndent(depth: Int) =
            0.until(depth)
                    .map { "\t" }
                    .toMutableList()
                    .apply { add("") }
                    .reduce { acc, s -> acc + s }


    private fun flattenConditions(): List<FeatureComponent> =
            conditions + conditions.flatMap {
                if (it.conditions.isNotEmpty()) it.flattenConditions()
                else emptyList()
            }

}