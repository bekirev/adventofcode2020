package com.abekirev.adventofcode2020.day22

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path
import java.util.LinkedList
import java.util.Queue

fun main() {
    partOne()
}

fun partOne() =
    println(
        CombatGame(
            Path.of("input", "day22", "first_deck.txt").useLinesFromResource { it.deck() },
            Path.of("input", "day22", "second_deck.txt").useLinesFromResource { it.deck() },
        ).winnerDeck().score()
    )

typealias SpaceCard = Int

interface Deck {
    val isEmpty: Boolean
    fun drawTopCard(): SpaceCard?
    fun add(cards: List<SpaceCard>)
    val cards: List<SpaceCard>
}

class QueueDeck private constructor(
    private val queue: Queue<SpaceCard>,
) : Deck {
    constructor(cards: Sequence<SpaceCard>) : this(
        LinkedList<SpaceCard>().apply {
            cards.forEach(::add)
        }
    )

    override val isEmpty: Boolean
        get() = queue.isEmpty()

    override fun drawTopCard(): SpaceCard? =
        queue.poll()

    override fun add(cards: List<SpaceCard>) {
        queue.addAll(cards)
    }

    override val cards: List<SpaceCard>
        get() = queue.toList()
}

class CombatGame(
    val firstPlayerDeck: Deck,
    val secondPlayerDeck: Deck,
) {
    fun round() {
        val firstPlayerCard = firstPlayerDeck.drawTopCard() ?: throw IllegalStateException("First player has no cards")
        val secondPlayerCard =
            secondPlayerDeck.drawTopCard() ?: throw IllegalStateException("Second player has no cards")
        when {
            firstPlayerCard > secondPlayerCard -> firstPlayerDeck.add(listOf(firstPlayerCard, secondPlayerCard))
            secondPlayerCard > firstPlayerCard -> secondPlayerDeck.add(listOf(secondPlayerCard, firstPlayerCard))
            else -> throw IllegalStateException("Two cards are the same")
        }
    }
}

fun CombatGame.winnerDeck(): Deck {
    tailrec fun CombatGame.winnerDeck(winnerDeck: Deck?): Deck =
        when (winnerDeck) {
            is Deck -> winnerDeck
            else -> {
                when {
                    firstPlayerDeck.isEmpty -> winnerDeck(secondPlayerDeck)
                    secondPlayerDeck.isEmpty -> winnerDeck(firstPlayerDeck)
                    else -> {
                        round()
                        winnerDeck(null)
                    }
                }
            }
        }
    return winnerDeck(null)
}

fun Deck.score(): Int {
    val cards = cards
    return cards.asSequence().zip(generateSequence(cards.size) { it - 1 })
        .map { (first, second) -> first * second }
        .sum()
}

fun Sequence<String>.deck(): Deck =
    QueueDeck(map(String::toInt))
