package com.abekirev.adventofcode2020.day10

import com.abekirev.adventofcode2020.util.pair
import com.abekirev.adventofcode2020.util.product
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
            val output = adapters.last() + DIFFERENCE_BETWEEN_LAST_AND_OUTPUT
            val fullList = listOf(INPUT_RATE)
                .plus(adapters)
                .plus(output)
            val validDifferenceRange = MIN_ADAPTER_DIFFERENCE..MAX_ADAPTER_DIFFERENCE
            adapterArrayVariantsCount(
                fullList,
                validDifferenceRange,
            )
        }
    )

private fun adapterArrayVariantsCount(
    fullList: List<Adapter>,
    validDifferenceRange: IntRange,
): Long {
    val indicesWithMaxDiffToTheNextElement = fullList.asSequence()
        .mapIndexed { index, adapter ->
            if (index != fullList.lastIndex && fullList[index + 1] - adapter == MAX_ADAPTER_DIFFERENCE) {
                index
            } else {
                null
            }
        }
        .filterNotNull()
        .toList()
    return formRanges(
        indicesWithMaxDiffToTheNextElement,
        fullList.size
    )
        .filter { it.second - it.first > 1 }
        .map {
            adapterArrayVariantsCount(
                AdapterCollection(
                    fullList.subList(
                        it.first,
                        it.second
                    ),
                    validDifferenceRange
                )
            )
        }
        .product()!!
}

private fun formRanges(
    indicesForListEnds: List<Int>,
    fullListSize: Int
): Sequence<Pair<Int, Int>> = sequence {
    var startIndex = 0
    for (index in indicesForListEnds) {
        yield(startIndex to index + 1)
        startIndex = index + 1
    }
    if (startIndex != fullListSize - 1) {
        yield(startIndex to fullListSize - 1)
    }
}

private fun adapterArrayVariantsCount(
    adapterCollection: AdapterCollection,
): Long = adapterArrayVariantsCount(
    adapterCollection,
    adapterCollection.first(),
)

private fun adapterArrayVariantsCount(
    adapterCollection: AdapterCollection,
    lastAdapter: Adapter,
): Long {
    return when (lastAdapter) {
        adapterCollection.last() -> 1
        else -> {
            val nextAdapters: Collection<Adapter> =
                adapterCollection.adaptersCanConnectWithElementsAtIndex(adapterCollection.adapterIndex(lastAdapter)!!)
            when {
                nextAdapters.isEmpty() -> 0
                else -> {
                    nextAdapters
                        .asSequence()
                        .map { nextAdapter ->
                            adapterArrayVariantsCount(
                                adapterCollection,
                                nextAdapter
                            )
                        }
                        .sum()
                }
            }
        }
    }
}

private class AdapterCollection private constructor(
    private val sortedAdapters: List<Adapter>,
    private val validDifferenceRange: IntRange,
) {
    constructor(
        adapters: Collection<Adapter>,
        validDifferenceRange: IntRange,
    ) : this(
        adapters.sorted(),
        validDifferenceRange,
    )

    private val adapterIndices: Map<Adapter, Int> by lazy {
        sortedAdapters.mapIndexed { i, a -> a to i }.toMap()
    }

    fun adaptersCanConnectWithElementsAtIndex(index: Int): Collection<Adapter> {
        val result = mutableSetOf<Adapter>()
        val adapterAtIndex = sortedAdapters[index]
        for (it in index + 1 until sortedAdapters.size) {
            val itAdapter = sortedAdapters[it]
            if (itAdapter - adapterAtIndex in validDifferenceRange) {
                result.add(
                    itAdapter
                )
            } else {
                break
            }
        }
        return result
    }

    fun adapterIndex(adapter: Adapter): Int? = adapterIndices[adapter]

    fun first(): Adapter = sortedAdapters.first()
    fun last(): Adapter = sortedAdapters.last()
}

private typealias Adapter = Int

private fun String.toAdapter(): Adapter = toInt()