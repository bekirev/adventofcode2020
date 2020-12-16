package com.abekirev.adventofcode2020.day15

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path
import java.util.Hashtable

fun main() {
    val startingNumbers = Path.of("input", "day15", "input.txt").useLinesFromResource { lines ->
        lines.first().split(",").map(String::toInt)
    }
    partOne(startingNumbers)
    partTwo(startingNumbers)
}

private fun partOne(startingNumbers: List<Int>) {
    println(
        memoryGameSpokenNumber(
            startingNumbers,
            2020
        )
    )
}

private fun partTwo(startingNumbers: List<Int>) {
    println(
        memoryGameSpokenNumber(
            startingNumbers,
            30000000
        )
    )
}

fun memoryGameSpokenNumber(startingNumbers: List<Int>, finalTurn: Int): Int {
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
                curTurn + 1,
            )
        }
    }
    check(finalTurn > 0) { "n should be positive" }
    return when {
        finalTurn <= startingNumbers.size -> startingNumbers[finalTurn - 1]
        else -> memoryGameSpokenNumber(
            startingNumbers.subList(0, startingNumbers.lastIndex)
                .mapIndexed { index: Int, spokenNumber: Int ->
                    spokenNumber to index + 1
                }
                .toMap(Hashtable()),
            startingNumbers.last(),
            startingNumbers.size + 1,
        )
    }
}