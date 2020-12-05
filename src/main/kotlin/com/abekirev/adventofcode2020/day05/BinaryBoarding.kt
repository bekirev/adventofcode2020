package com.abekirev.adventofcode2020.day05

import com.abekirev.adventofcode2020.util.linesFromResource
import java.nio.file.Path
import kotlin.streams.asSequence

fun main() {
    partOne()
}

private fun partOne() {
    println(
        input()
            .map(Seat::id)
            .maxOrNull()
    )
}

private fun input() =
    Path.of("input", "day05", "input.txt")
        .linesFromResource()
        .asSequence()
        .map(String::toSeat)

data class Seat(
    val row: Int,
    val col: Int,
) {
    val id by lazy { row * 8 + col }
}

fun String.toSeat(): Seat {
    if (length != 10) throw IllegalArgumentException("Wrong format: $this")
    return Seat(
        substring(0..6).toRow(),
        substring(7..9).toCol()
    )
}

fun String.toRow(): Int =
    this
        .replace("F", "0")
        .replace("B", "1")
        .toInt(2)

fun String.toCol(): Int =
    this
        .replace("L", "0")
        .replace("R", "1")
        .toInt(2)
