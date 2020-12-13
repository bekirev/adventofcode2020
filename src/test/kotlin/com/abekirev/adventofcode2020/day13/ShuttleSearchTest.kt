package com.abekirev.adventofcode2020.day13

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class ShuttleSearchTest : ShouldSpec({
    should("return first matching number and its divider from provided dividers collections after specific number") {
        forAll(
            row(1, setOf(4, 5), NumberDividerPair(4, 4)),
            row(1, setOf(5, 4), NumberDividerPair(4, 4)),
            row(3, setOf(3, 2), NumberDividerPair(3, 3)),
            row(4, setOf(3, 2), NumberDividerPair(4, 2)),
            row(20, setOf(13, 7, 11), NumberDividerPair(21, 7)),
            row(20, setOf(11, 19, 23), NumberDividerPair(22, 11)),
        ) { number, dividers, result ->
            firstNumberDividerPair(number, dividers) shouldBe result
        }
    }

    should("pass call to NumberRule::check") {
        val dividerNumberRule = mock<DividerNumberRule>()
        1.toBigInteger() compliesWith dividerNumberRule
        verify(dividerNumberRule, times(1)).check(1.toBigInteger())
    }

    should("check if a number is divided by divider") {
        forAll(
            row(1, 1, true),
            row(1, 2, false),
            row(2, 1, true),
            row(3, 6, false),
            row(6, 3, true),
            row(2, 4, false),
            row(7, 11, false),
        ) { number, divider, result ->
            number.toBigInteger() compliesWith CurrentDividerNumberRule(divider.toBigInteger()) shouldBe result
        }
    }

    should("check if a number with offset is divided by divider") {
        forAll(
            row(1, 1, 1, true),
            row(1, 1, 2, true),
            row(1, 2, 2, false),
            row(2, 2, 2, true),
            row(4, -2, 2, true),
            row(4, -3, 2, false),
            row(5, 2, 7, true),
        ) { number, offset, divider, result ->
            number.toBigInteger() compliesWith OffsetDividerNumberRule(divider.toBigInteger(),
                offset.toBigInteger()) shouldBe result
        }
    }

    should("return set of numbers from list with an offset position to the highest number") {
        forAll(
            row(0, listOf(1, 5, 3), setOf(1 to 0, 5 to 1, 3 to 2)),
            row(2, listOf(3, 5, 1), setOf(3 to -2, 5 to -1, 1 to 0)),
            row(1, listOf(6, 0, 2, 5), setOf(6 to -1, 0 to 0, 2 to 1, 5 to 2)),
            row(1, listOf(11, null, 5, null, 7), setOf(11 to -1, 5 to 1, 7 to 3)),
            row(2, listOf(3, null, 99, null, null, 5), setOf(3 to -2, 99 to 0, 5 to 3)),
            row(6, listOf(null, 76, null, 3, null, null, 101, null), setOf(76 to -5, 3 to -3, 101 to 0)),
        ) { index, list, result ->
            list.offsetsFromGivenIndexFromOtherElements(index).toSet() shouldBe result
        }
    }
})