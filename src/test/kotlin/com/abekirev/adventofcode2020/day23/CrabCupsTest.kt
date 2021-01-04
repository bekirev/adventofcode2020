package com.abekirev.adventofcode2020.day23

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class CrabCupsTest : ShouldSpec({
    should("perform moves") {
        val resPair = move(listOf(3, 8, 9, 1, 2, 5, 4, 6, 7), 10)
        val expectedResult = listOf(8, 3, 7, 4, 1, 9, 2, 6, 5)
        resPair.second shouldBe expectedResult.first()
        resPair.first.removeAfter(resPair.second, expectedResult.size - 1) shouldBe expectedResult.subList(1, expectedResult.size)
    }
})