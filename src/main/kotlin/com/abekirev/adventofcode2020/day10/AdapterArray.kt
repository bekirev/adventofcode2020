package com.abekirev.adventofcode2020.day10

import com.abekirev.adventofcode2020.util.pair
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

private const val INPUT_RATE = 0
private const val DIFFERENCE_BETWEEN_LAST_AND_OUTPUT = 3

private fun partOne() =
    println(
        Path.of("input", "day10", "input.txt").useLinesFromResource { lines ->
            val adapters = lines
                .map(String::toAdapter)
                .sorted()
                .toList()
            val differencesCount = sequenceOf(
                sequenceOf(INPUT_RATE),
                adapters.asSequence(),
                sequenceOf(adapters.last() + DIFFERENCE_BETWEEN_LAST_AND_OUTPUT)
            ).flatten()
                .pair()
                .map { it.second - it.first }
                .groupingBy { it }
                .eachCount()
            differencesCount[3]!! * differencesCount[1]!!
        }
    )

private const val MIN_ADAPTER_DIFFERENCE = 1
private const val MAX_ADAPTER_DIFFERENCE = 3

private fun partTwo() =
    println(
        Path.of("input", "day10", "input.txt").useLinesFromResource { lines ->
            val adapters = lines
                .map(String::toAdapter)
                .sorted()
                .toList()
            val pathsCounts = mutableMapOf(
                0 to 1L
            )
            adapters.forEach { adapter ->
                pathsCounts[adapter] = (MIN_ADAPTER_DIFFERENCE..MAX_ADAPTER_DIFFERENCE)
                    .mapNotNull { difference -> pathsCounts[adapter - difference] }
                    .sum()
            }
            pathsCounts[adapters.last()]
        }
    )

private typealias Adapter = Int

private fun String.toAdapter(): Adapter = toInt()