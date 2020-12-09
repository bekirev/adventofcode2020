package com.abekirev.adventofcode2020.day02

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class CharacterEntryPositionPasswordPolicyTest : ShouldSpec({
    should("validate that a character meets in a certain positions of a password exactly one time") {
        forAll(
            row(setOf(0, 2), 'a', "abcde", true),
            row(setOf(0, 2), 'b', "cdefg", false),
            row(setOf(1, 8), 'c', "ccccccccc", false),
        ) { positions, char, password, result ->
            CharacterEntryPositionPasswordPolicy(char, positions).checkPassword(password) shouldBe result
        }
    }
})