package com.abekirev.adventofcode2020.day01

import com.abekirev.adventofcode2020.util.linesFromResource
import java.nio.file.Path
import kotlin.streams.asSequence

fun main() {
    partOne()
}

private fun partOne() {
    Path.of("input", "day01", "input.txt")
        .linesFromResource()
        .asSequence()
        .map(String::toInt)
        .findPairWithSum(2020)
        .let { pair ->
            println(
                if (pair != null)
                    pair.first * pair.second
                else
                    "No such entries"
            )
        }
}

private fun Sequence<Int>.findPairWithSum(sum: Int): Pair<Int, Int>? {
    return pairs()
        .firstOrNull { (a, b) -> a + b == sum }
}

private fun <T> Sequence<T>.pairs(): Sequence<Pair<T, T>> {
    return sequence {
        val prevElems = mutableSetOf<T>()
        val iterator = this@pairs.iterator()
        if (iterator.hasNext()) {
            prevElems += iterator.next()
        }
        while (iterator.hasNext()) {
            val elem = iterator.next()
            yieldAll(
                prevElems.map { it to elem }
            )
            prevElems += elem
        }
    }
}