package com.abekirev.adventofcode2020.day15

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

private fun partOne() {
    println(
        result(2020)
    )
}

private fun partTwo() {
    println(
        result(30000000)
    )
}

private fun result(finalTurn: Int) = Path.of("input", "day15", "input.txt").useLinesFromResource { lines ->
    memoryGameSpokenNumber(
        lines.first().split(",").map(String::toInt),
        finalTurn
    )
}

fun memoryGameSpokenNumber(startingSequence: List<Int>, finalTurn: Int): Int {
    tailrec fun memoryGameSpokenNumber(
        prevNumbers: MutableMap<Int, Int>,
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
                prevNumbers.apply {
                    put(mostRecentSpokenNumber, curTurn - 1)
                },
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
                .toMap(mutableMapOf()),
            startingSequence.last(),
            startingSequence.size + 1,
        )
    }
}