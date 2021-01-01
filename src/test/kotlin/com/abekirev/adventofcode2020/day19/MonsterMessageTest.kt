package com.abekirev.adventofcode2020.day19

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class MonsterMessageTest : ShouldSpec({
    should("Should match simple rule") {
        val wordChecker = wordChecker(
            sequenceOf(
                "0: 1 2",
                "1: \"a\"",
                "2: 1 3 | 3 1",
                "3: \"b\"",
            ),
        )
        forAll(
            row("aab", true)
        ) { word, result ->
            wordChecker.check(word.tokenized()) shouldBe result
        }
    }
    should("Should match recursive rule") {
        val wordChecker = wordChecker(
            sequenceOf(
                "0: 3",
                "1: \"a\"",
                "2: \"b\"",
                "3: 2 | 1 3 1",
            )
        )
        forAll(
            row("aba", true),
            row("aabaa", true),
        ) { word, result ->
            wordChecker.check(word.tokenized()) shouldBe result
        }
    }
})