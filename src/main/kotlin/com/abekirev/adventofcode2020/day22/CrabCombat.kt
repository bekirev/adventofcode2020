package com.abekirev.adventofcode2020.day22

import com.abekirev.adventofcode2020.day22.Winner.FIRST
import com.abekirev.adventofcode2020.day22.Winner.SECOND
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path
import java.util.LinkedList
import java.util.Queue

fun main() {
    partOne()
    partTwo()
}

fun partOne() =
    println(
        CombatGame(
            firstPlayerDeck(),
            secondPlayerDeck(),
        ).winnerDeck().score()
    )

fun partTwo() =
    println(
        RecursiveCombat(
            firstPlayerDeck(),
            secondPlayerDeck(),
        ).winnerDeck().score()
    )

private fun secondPlayerDeck() = Path.of("input", "day22", "second_deck.txt").useLinesFromResource { it.deck() }

private fun firstPlayerDeck() = Path.of("input", "day22", "first_deck.txt").useLinesFromResource { it.deck() }

typealias SpaceCard = Int

interface Deck {
    val isEmpty: Boolean
    val size: Int
    fun drawTopCard(): SpaceCard?
    fun add(cards: List<SpaceCard>)
    val cards: List<SpaceCard>
    fun copy(depth: Int): Deck
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

    override val size: Int by queue::size

    override fun drawTopCard(): SpaceCard? =
        queue.poll()

    override fun add(cards: List<SpaceCard>) {
        queue.addAll(cards)
    }

    override val cards: List<SpaceCard>
        get() = queue.toList()

    override fun copy(depth: Int): Deck =
        QueueDeck(queue.asSequence().take(depth))
}

interface Game {
    val firstPlayerDeck: Deck
    val secondPlayerDeck: Deck
    fun round()
}


enum class Winner {
    FIRST,
    SECOND,
    ;
}

fun Game.winner(): Winner {
    tailrec fun Game.winner(winner: Winner?): Winner =
        when (winner) {
            is Winner -> winner
            else -> {
                when {
                    firstPlayerDeck.isEmpty -> winner(SECOND)
                    secondPlayerDeck.isEmpty -> winner(FIRST)
                    else -> {
                        round()
                        winner(null)
                    }
                }
            }
        }
    return winner(null)
}

fun Game.winnerDeck(): Deck = when(winner()) {
    FIRST -> firstPlayerDeck
    SECOND -> secondPlayerDeck
}

class CombatGame(
    override val firstPlayerDeck: Deck,
    override val secondPlayerDeck: Deck,
) : Game {
    override fun round() {
        val firstPlayerCard = firstPlayerDeck.drawTopCard() ?: throw IllegalStateException("First player has no cards")
        val secondPlayerCard = secondPlayerDeck.drawTopCard() ?: throw IllegalStateException("Second player has no cards")
        when {
            firstPlayerCard > secondPlayerCard -> firstPlayerDeck.add(listOf(firstPlayerCard, secondPlayerCard))
            secondPlayerCard > firstPlayerCard -> secondPlayerDeck.add(listOf(secondPlayerCard, firstPlayerCard))
            else -> throw IllegalStateException("Two cards are the same")
        }
    }
}

class DeckRepository private constructor(
    private val set: MutableSet<Int>,
) {
    constructor() : this(hashSetOf())

    fun add(deck: Deck): Boolean =
        set.add(deck.cards.hashCode())
}

class RecursiveCombat(
    override val firstPlayerDeck: Deck,
    override val secondPlayerDeck: Deck,
) : Game {
    private val firstDeckRepository = DeckRepository()
    private val secondDeckRepository = DeckRepository()

    override fun round() {
        if (!(firstDeckRepository.add(firstPlayerDeck) and secondDeckRepository.add(secondPlayerDeck))) {
            firstPlayerDeck.add(listOf(
                firstPlayerDeck.drawTopCard() ?: throw IllegalStateException("First player has no cards"),
                secondPlayerDeck.drawTopCard() ?: throw IllegalStateException("Second player has no cards"),
            ))
        } else {
            val firstPlayerCard = firstPlayerDeck.drawTopCard() ?: throw IllegalStateException("First player has no cards")
            val secondPlayerCard = secondPlayerDeck.drawTopCard() ?: throw IllegalStateException("Second player has no cards")
            if (firstPlayerDeck.size >= firstPlayerCard && secondPlayerDeck.size >= secondPlayerCard) {
                when (RecursiveCombat(firstPlayerDeck.copy(firstPlayerCard), secondPlayerDeck.copy(secondPlayerCard)).winner()) {
                    FIRST -> firstPlayerDeck.add(listOf(firstPlayerCard, secondPlayerCard))
                    SECOND -> secondPlayerDeck.add(listOf(secondPlayerCard, firstPlayerCard))
                }
            } else {
                when {
                    firstPlayerCard > secondPlayerCard -> firstPlayerDeck.add(listOf(firstPlayerCard, secondPlayerCard))
                    secondPlayerCard > firstPlayerCard -> secondPlayerDeck.add(listOf(secondPlayerCard, firstPlayerCard))
                    else -> throw IllegalStateException("Two cards are the same")
                }
            }
        }
    }
}

fun Deck.score(): Int {
    val cards = cards
    return cards.asSequence().zip(generateSequence(cards.size) { it - 1 })
        .map { (first, second) -> first * second }
        .sum()
}

fun Sequence<String>.deck(): Deck =
    QueueDeck(map(String::toInt))
