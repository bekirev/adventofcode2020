package com.abekirev.adventofcode2020.day09

import com.abekirev.adventofcode2020.day01.findTupleWithSum
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path
import java.util.LinkedList
import java.util.Queue

fun main() {
    partOne()
}

private const val PREAMBLE_SIZE = 26

private fun partOne() =
    println(
        Path.of("input", "day09", "input.txt").useLinesFromResource { lines ->
            lines
                .map(String::toLong)
                .runningChunked(PREAMBLE_SIZE)
                .map { it.last() to it.asSequence().take(PREAMBLE_SIZE - 1).findTupleWithSum(it.last(), 2) }
                .filter { it.second == null }
                .firstOrNull()
                ?.first
        }
    )

private fun <T> Sequence<T>.runningChunked(chunkSize: Int): Sequence<List<T>> = sequence {
    val queue: Queue<T> = LinkedList()
    for (elem in this@runningChunked) {
        if (queue.size != chunkSize) {
            queue.add(elem)
        } else {
            queue.remove()
            queue.add(elem)
            yield(queue.toList())
        }
    }
}
