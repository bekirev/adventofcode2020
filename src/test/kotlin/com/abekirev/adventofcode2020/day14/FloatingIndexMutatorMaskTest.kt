package com.abekirev.adventofcode2020.day14

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class FloatingIndexMutatorMaskTest : ShouldSpec({
    should("mutate given bit") {
        forAll(
            row(0b1L, 0, setOf(0b0L, 0b1L)),
            row(0b01L, 1, setOf(0b01L, 0b11L)),
            row(0b10L, 1, setOf(0b10L, 0b00L)),
            row(0b0L, 3, setOf(0b1000L, 0b0000L)),
        ) { value, index, result ->
            FloatingIndicesMutatorMask(index).mutate(value).toSet() shouldBe result
        }
    }

    should("mutate given bits") {
        forAll(
            row(0b1L, 0, 1, setOf(0b00L, 0b01L, 0b10L, 0b11L)),
            row(0b01L, 1, 2, setOf(0b001L, 0b011L, 0b101, 0b111L)),
            row(0b000, 0, 2, setOf(0b000L, 0b001L, 0b100, 0b101L)),
        ) { value, index1, index2, result ->
            FloatingIndicesMutatorMask(index1)
                .andThen(FloatingIndicesMutatorMask(index2))
                .mutate(value).toSet() shouldBe result
        }
    }

    should("mutate given indices and apply OrBitMap") {
        OrBitMask(0b010010L).andThen(
            FloatingIndicesMutatorMask(0)
        ).mutate(0b101010L).toSet() shouldBe setOf(0b111010L, 0b111011L)
    }
})
