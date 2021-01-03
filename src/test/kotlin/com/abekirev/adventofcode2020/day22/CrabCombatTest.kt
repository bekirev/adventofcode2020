package com.abekirev.adventofcode2020.day22

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe

class CrabCombatTest : ShouldSpec({
    should("create deck with empty cards") {
        val deck = QueueDeck(emptySequence())
        deck.isEmpty shouldBe true
        deck.cards shouldBe emptyList()
        deck.drawTopCard() shouldBe null
        deck.isEmpty shouldBe true
        deck.cards shouldBe emptyList()
    }
    should("place place single card on the top") {
        val deck = QueueDeck(sequenceOf(6))
        deck.isEmpty shouldBe false
        deck.cards shouldBe listOf(6)
        deck.drawTopCard() shouldBe 6
        deck.isEmpty shouldBe true
        deck.cards shouldBe emptyList()
    }
    should("place first card on the top") {
        val deck = QueueDeck(sequenceOf(7, 3, 5))
        deck.isEmpty shouldBe false
        deck.cards shouldBe listOf(7, 3, 5)
        deck.drawTopCard() shouldBe 7
        deck.isEmpty shouldBe false
        deck.cards shouldBe listOf(3, 5)
        deck.drawTopCard() shouldBe 3
        deck.cards shouldBe listOf(5)
        deck.isEmpty shouldBe false
        deck.drawTopCard() shouldBe 5
        deck.isEmpty shouldBe true
        deck.cards shouldBe emptyList()
    }
    should("place elements on the bottom of the empty deck") {
        val deck = QueueDeck(emptySequence())
        deck.isEmpty shouldBe true
        deck.cards shouldBe emptyList()
        deck.add(listOf(32, 11))
        deck.isEmpty shouldBe false
        deck.cards shouldBe listOf(32, 11)
        deck.drawTopCard() shouldBe 32
        deck.isEmpty shouldBe false
        deck.cards shouldBe listOf(11)
        deck.drawTopCard() shouldBe 11
        deck.isEmpty shouldBe true
        deck.cards shouldBe emptyList()
    }
    should("place elements on the bottom of the non empty deck") {
        val deck = QueueDeck(sequenceOf(4))
        deck.isEmpty shouldBe false
        deck.cards shouldBe listOf(4)
        deck.add(listOf(102, 5))
        deck.isEmpty shouldBe false
        deck.cards shouldBe listOf(4, 102, 5)
        deck.drawTopCard() shouldBe 4
        deck.isEmpty shouldBe false
        deck.cards shouldBe listOf(102, 5)
        deck.drawTopCard() shouldBe 102
        deck.isEmpty shouldBe false
        deck.cards shouldBe listOf(5)
        deck.drawTopCard() shouldBe 5
        deck.isEmpty shouldBe true
        deck.cards shouldBe emptyList()
    }
    should("throw IllegalStateException if first player has no cards left") {
        val firstPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn null
        }
        val secondPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn 2
        }
        val game = CombatGame(
            firstPlayerDeck,
            secondPlayerDeck
        )
        shouldThrow<IllegalStateException> {
            game.round()
        }
    }
    should("throw IllegalStateException if second player has no cards left") {
        val firstPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn 5
        }
        val secondPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn null
        }
        val game = CombatGame(
            firstPlayerDeck,
            secondPlayerDeck
        )
        shouldThrow<IllegalStateException> {
            game.round()
        }
    }
    should("play round when first player wins") {
        val firstPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn 9
        }
        val secondPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn 5
        }
        val game = CombatGame(
            firstPlayerDeck,
            secondPlayerDeck,
        )
        game.round()
        verify(firstPlayerDeck, times(1)).drawTopCard()
        verify(secondPlayerDeck, times(1)).drawTopCard()
        verify(firstPlayerDeck, times(1)).add(eq(listOf(9, 5)))
        verify(secondPlayerDeck, times(0)).add(any())
    }
    should("play round when second player wins") {
        val firstPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn 4
        }
        val secondPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn 11
        }
        val game = CombatGame(
            firstPlayerDeck,
            secondPlayerDeck,
        )
        game.round()
        verify(firstPlayerDeck, times(1)).drawTopCard()
        verify(secondPlayerDeck, times(1)).drawTopCard()
        verify(secondPlayerDeck, times(1)).add(eq(listOf(11, 4)))
        verify(firstPlayerDeck, times(0)).add(any())
    }
    should("play round with exception if players have the same card") {
        val firstPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn 5
        }
        val secondPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn 5
        }
        val game = CombatGame(
            firstPlayerDeck,
            secondPlayerDeck,
        )
        shouldThrow<IllegalStateException> {
            game.round()
        }
        verify(firstPlayerDeck, times(1)).drawTopCard()
        verify(secondPlayerDeck, times(1)).drawTopCard()
        verify(secondPlayerDeck, times(0)).add(any())
        verify(firstPlayerDeck, times(0)).add(any())
    }
    should("return first player's deck when second player has no cards") {
        val firstPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn 32
            on { isEmpty } doReturn false
        }
        val secondPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn null
            on { isEmpty } doReturn true
        }
        val game = CombatGame(
            firstPlayerDeck,
            secondPlayerDeck,
        )
        game.winnerDeck() shouldBe firstPlayerDeck
    }
    should("return second player's deck when first player has no cards") {
        val firstPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn null
            on { isEmpty } doReturn true
        }
        val secondPlayerDeck = mock<Deck>() {
            on { drawTopCard() } doReturn 10
            on { isEmpty } doReturn false
        }
        val game = CombatGame(
            firstPlayerDeck,
            secondPlayerDeck,
        )
        game.winnerDeck() shouldBe secondPlayerDeck
    }
    should("calculate deck score on empty deck") {
        val deck = QueueDeck(emptySequence())
        deck.score() shouldBeExactly 0
    }
    should("calculate deck score on deck with one element") {
        val deck = QueueDeck(sequenceOf(4))
        deck.score() shouldBeExactly 4
    }
    should("calculate deck score on deck with multiple elements") {
        val deck = QueueDeck(sequenceOf(3, 10, 17))
        deck.score() shouldBeExactly (3 * 3 + 10 * 2 + 17)
    }
    should("parse deck from empty sequence") {
        val deck = emptySequence<String>().deck()
        deck.isEmpty shouldBe true
    }
    should("parse deck from non empty sequence") {
        val deck = sequenceOf(
            "123",
            "546",
            "1"
        ).deck()
        deck.isEmpty shouldBe false
        deck.cards shouldBe listOf(123, 546, 1)
    }
})
