package com.abekirev.adventofcode2020.day02

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class CharacterEntryCountPasswordPolicyTest : ShouldSpec({
    should("validate that a password contains a character only given number of time") {
        forAll(
            row(1..3, 'a', "abcde", true),
            row(1..3, 'b', "cdefg", false),
            row(2..9, 'c', "ccccccccc", true),
        ) { range, char, password, result ->
            CharacterEntryCountPasswordPolicy(char, range).checkPassword(password) shouldBe result
        }
    }
})