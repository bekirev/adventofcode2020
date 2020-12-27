package com.abekirev.adventofcode2020.day18

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.longs.shouldBeExactly

class OperationOrderTest : ShouldSpec({
    should("calculate value of arithmetic expression with equal priority for addition and multiplication") {
        forAll(
            row("1 + 2 * 3 + 4 * 5 + 6", 71L),
            row("1 + (2 * 3) + (4 * (5 + 6))", 51L),
            row("2 * 3 + (4 * 5)", 26L),
            row("5 + (8 * 3 + 9 + 3 * 4 * 3)", 437L),
            row("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))", 12240L),
            row("((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2", 13632L),
        ) { expression, result ->
            expression.tokenized().toExpressionEqualPriority().eval() shouldBeExactly result
        }
    }
})