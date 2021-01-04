package com.abekirev.adventofcode2020.day23

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class CrabCupsTest : ShouldSpec({
    should("return labels after 1 element after given number of moves") {
        forAll(
            row(listOf(3, 8, 9, 1, 2, 5, 4, 6, 7), 10, listOf(9, 2, 6, 5, 8, 3, 7, 4)),
            row(listOf(3, 8, 9, 1, 2, 5, 4, 6, 7), 100, listOf(6, 7, 3, 8, 4, 5, 2, 9)),
        ) { input, movesCount, result ->
            input.labelsAfterOne(movesCount) shouldBe result
        }
    }
    should("perform moves") {
        listOf(3, 8, 9, 1, 2, 5, 4, 6, 7).after(10) shouldBe listOf(8, 3, 7, 4, 1, 9, 2, 6, 5)
    }
    should("should perform move") {
        forAll(
            row(
                listOf(3, 8, 9, 1, 2, 5, 4, 6, 7),
                listOf(2, 8, 9, 1, 5, 4, 6, 7, 3),
            ),
            row(
                listOf(2, 8, 9, 1, 5, 4, 6, 7, 3),
                listOf(5, 4, 6, 7, 8, 9, 1, 3, 2),
            ),
            row(
                listOf(5, 4, 6, 7, 8, 9, 1, 3, 2),
                listOf(8, 9, 1, 3, 4, 6, 7, 2, 5),
            ),
        ) { input, result ->
            move(input) shouldBe result
        }
    }
    should("reorder circled with start element") {
        forAll(
            row(listOf(1), 1, listOf(1)),
            row(listOf(1, 2), 1, listOf(1, 2)),
            row(listOf(4, 2, 3), 2, listOf(2, 3, 4)),
            row(listOf(8, 5, 6), 6, listOf(6, 8, 5)),
            row(listOf(10, 4, 6, 4), 4, listOf(4, 6, 4, 10)),
        ) { input, elem, result ->
            input.reorderCircledWithStartElement(elem) shouldBe result
        }
    }
    should("determine destination cup") {
        forAll(
            row(listOf(6, 4, 3, 7, 2, 5), listOf(4, 3), 5),
            row(listOf(4, 3, 6, 7, 2, 5), listOf(3, 6), 2),
            row(listOf(3, 1, 2, 4, 6, 5), listOf(1, 2), 6),
        ) { cups, pickup, result ->
            cups.destinationCup(pickup) shouldBe result
        }
    }
    should("place list of elements after elem") {
        forAll(
            row(sequenceOf(4), 3, listOf(1), listOf(4)),
            row(sequenceOf(5), 5, listOf(), listOf(5)),
            row(sequenceOf(3, 2, 3), 3, listOf(1, 2), listOf(3, 1, 2, 2, 3, 1, 2)),
        ) { input, elem, list, result ->
            input.placeAfter(elem, list).toList() shouldBe result
        }
    }
    should("reorder circled with start index") {
        forAll(
            row(listOf(1, 6, 3), 0, listOf(1, 6, 3)),
        ) { input, index, result ->
            input.reorderCircledWithStartIndex(index) shouldBe result
        }
    }
})