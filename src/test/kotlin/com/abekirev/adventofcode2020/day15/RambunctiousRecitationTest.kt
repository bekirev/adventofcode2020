package com.abekirev.adventofcode2020.day15

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.ints.shouldBeExactly

class RambunctiousRecitationTest : ShouldSpec({
    should("return spoken number from starting numbers if requested index within its range") {
        forAll(
            row(listOf(5), 1, 5),
            row(listOf(3, 1, 5), 1, 3),
            row(listOf(8, 6, 2), 2, 6),
            row(listOf(7, 3), 2, 3),
            row(listOf(43, 22, 123), 3, 123),
        ) { startingSequence, number, result ->
            memoryGameSpokenNumber(startingSequence, number) shouldBeExactly result
        }
    }

    should("return spoken number after the starting numbers according to previous number age") {
        forAll(
            row(listOf(5), 2, 0),
            row(listOf(6, 3), 3, 0),
            row(listOf(0, 3, 6), 4, 0),
            row(listOf(0, 3, 6), 5, 3),
            row(listOf(0, 3, 6), 6, 3),
            row(listOf(0, 3, 6), 7, 1),
            row(listOf(0, 3, 7), 8, 0),
            row(listOf(0, 3, 7), 9, 4),
            row(listOf(0, 3, 7), 10, 0),
            row(listOf(0, 3, 6), 2020, 436),
        ) { startingSequence, number, result ->
            memoryGameSpokenNumber(startingSequence, number) shouldBeExactly result
        }
    }
})