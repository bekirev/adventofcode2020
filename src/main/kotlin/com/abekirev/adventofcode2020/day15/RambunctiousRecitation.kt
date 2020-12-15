package com.abekirev.adventofcode2020.day15

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
}

private fun partOne() =
    println(
        Path.of("input", "day15", "input.txt").useLinesFromResource { lines ->
            memoryGameSpokenNumber(
                lines.first().split(",").map(String::toInt),
                2020
            )
        }
    )

fun memoryGameSpokenNumber(startingSequence: List<Int>, finalTurn: Int): Int {
    fun memoryGameSpokenNumber(
        prevNumbers: Map<Int, Int>,
        mostRecentSpokenNumber: Int,
        curTurn: Int,
    ): Int {
        fun nextSpokenNumber(): Int =
            when (val lastNSpoken = prevNumbers[mostRecentSpokenNumber]) {
                is Int -> curTurn - 1 - lastNSpoken
                else -> 0
            }

        val nextSpokenNumber = nextSpokenNumber()
        return when (curTurn) {
            finalTurn -> nextSpokenNumber
            else -> memoryGameSpokenNumber(
                prevNumbers.plus(mostRecentSpokenNumber to curTurn - 1),
                nextSpokenNumber,
                curTurn + 1
            )
        }
    }
    check(finalTurn > 0) { "n should be positive" }
    return when {
        finalTurn <= startingSequence.size -> startingSequence[finalTurn - 1]
        else -> memoryGameSpokenNumber(
            startingSequence.subList(0, startingSequence.lastIndex)
                .mapIndexed { index: Int, spokenNumber: Int ->
                    spokenNumber to index + 1
                }
                .toMap(),
            startingSequence.last(),
            startingSequence.size + 1,
        )
    }
}