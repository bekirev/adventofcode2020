package com.abekirev.adventofcode2020.day16

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class TicketTranslationTest : ShouldSpec({
    should("check if number is within bounds") {
        forAll(
            row(1..3, 2, true),
            row(4..5, 4, true),
            row(2..6, 6, true),
            row(8..8, 8, true),
            row(10..10, 9, false),
            row(2..2, 3, false),
            row(7..12, 6, false),
            row(5..10, 11, false),
        ) { range, number, result ->
            number compliesWith RangeNumberCheck(range) shouldBe result
        }
    }

    should("check if number complies with one of number checks") {
        val firstFalseCheck = mock<NumberCheck>() {
            on { check(any()) } doReturn false
        }
        val secondFalseCheck = mock<NumberCheck>() {
            on { check(any()) } doReturn false
        }
        val firstTrueCheck = mock<NumberCheck>() {
            on { check(any()) } doReturn true
        }
        val secondTrueCheck = mock<NumberCheck>() {
            on { check(any()) } doReturn true
        }
        (firstFalseCheck or secondFalseCheck).check(2) shouldBe false
        (firstFalseCheck or firstTrueCheck).check(7) shouldBe true
        (firstTrueCheck or secondFalseCheck).check(1) shouldBe true
        (firstTrueCheck or secondTrueCheck).check(3) shouldBe true
    }

    should("parse field name with two ranges") {
        forAll(
            row("class: 1-3 or 5-7", "class", 1..3, 5..7),
            row("row: 6-11 or 33-44", "row", 6..11, 33..44),
            row("seat: 13-40 or 45-50", "seat", 13..40, 45..50),
        ) { inputStr, field, firstRange, secondRange ->
            val rule = RuleParserImpl.parse(inputStr)
            rule.field shouldBe field
            rule.firstRange shouldBe firstRange
            rule.secondRange shouldBe secondRange
        }
    }

    should("create number check from rule") {
        forAll(
            row(
                RuleImpl("kjfsf", 2..4, 7..10),
                setOf(2, 3, 4, 7, 8, 9, 10),
                setOf(-99, -6, 0, 1, 5, 6, 11, 12, 20, 100)
            ),
            row(
                RuleImpl("fdsnfds", -6..1, 5..8),
                setOf(-6, -5, -4, -3, -2, -1, 0, 1, 5, 6, 7, 8),
                setOf(-323, -10, -8, -7, 2, 3, 4, 9, 10, 12, 220, 1000)
            ),
        ) { rule, trueSet, falseSet ->
            val numberCheck = rule.toNumberCheck()
            trueSet.forEach { it compliesWith numberCheck shouldBe true }
            falseSet.forEach { it compliesWith numberCheck shouldBe false }
        }
    }

    should("parse ticket") {
        forAll(
            row("7,3,47", listOf(7, 3, 47)),
            row("40,4,50,76", listOf(40, 4, 50, 76)),
            row("2,1", listOf(2, 1)),
            row("101", listOf(101)),
        ) { inputStr, result ->
            TicketParserImpl.parse(inputStr).numbers shouldBe result
        }
    }
})
