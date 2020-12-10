package com.abekirev.adventofcode2020.day10

import com.abekirev.adventofcode2020.util.pair
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
}

private fun partOne() =
    println(
        Path.of("input", "day10", "input.txt").useLinesFromResource { lines ->
            val adapters = lines
                .map(String::toAdapter)
                .sorted()
                .toList()
            val differencesCount = sequenceOf(
                sequenceOf(0),
                adapters.asSequence(),
                sequenceOf(adapters.last() + 3)
            ).flatten()
                .pair()
                .map { it.second - it.first }
                .groupingBy { it }
                .eachCount()
            differencesCount[3]!! * differencesCount[1]!!
        }
    )

private typealias Adapter = Int

private fun String.toAdapter(): Adapter = toInt()