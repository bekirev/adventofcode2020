package com.abekirev.adventofcode2020.day09

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path
import java.util.LinkedList
import java.util.Queue


fun main() {
    val exceptionalNumber = partOne() ?: throw IllegalStateException("Exceptional number is not found")
    println(exceptionalNumber)
    val encryptionWeakness =
        partTwo(exceptionalNumber) ?: throw IllegalStateException("Encryption weakness is not found")
    println(encryptionWeakness)
}

private const val PREAMBLE_SIZE = 25

private fun partOne() =
    Path.of("input", "day09", "input.txt").useLinesFromResource { lines ->
        lines
            .map(String::toLong)
            .exceptionalNumber(PREAMBLE_SIZE)
    }

private fun partTwo(exceptionalNumber: Long) =
    Path.of("input", "day09", "input.txt").useLinesFromResource { lines ->
        lines.map(String::toLong)
            .findSubsequenceWithSpecificSumOfElements(exceptionalNumber)
            ?.let { it.minOrNull()!! to it.maxOrNull()!! }
            ?.let { it.first + it.second }
    }

private fun Sequence<Long>.findSubsequenceWithSpecificSumOfElements(sum: Long): Collection<Long>? {
    val slice: Queue<Long> = LinkedList()
    for (elem in this) {
        slice.add(elem)
        val sliceSum = { slice.sum() }
        while (sliceSum() > sum)
            slice.remove()
        if (sliceSum() == sum && slice.size > 1)
            return slice
    }
    return null
}

private fun Sequence<Long>.exceptionalNumber(preambleSize: Int): Long? {
    val queue: Queue<Long> = LinkedList()
    for (elem in this) {
        if (queue.size == preambleSize) {
            if (!queue.hasTwoNumbersWithSpecificSum(elem))
                return elem
            queue.remove()
        }
        queue.add(elem)
    }
    return null
}


private fun Collection<Long>.hasTwoNumbersWithSpecificSum(sum: Long): Boolean {
    val set = toSet()
    return set.any { number -> sum - number != number && sum - number in set }
}