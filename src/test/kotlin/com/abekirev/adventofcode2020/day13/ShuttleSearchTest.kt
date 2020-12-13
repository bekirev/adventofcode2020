package com.abekirev.adventofcode2020.day13

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
})