package com.abekirev.adventofcode2020.day05

import com.abekirev.adventofcode2020.util.linesFromResource
import java.nio.file.Path
import kotlin.streams.asSequence

fun main() {
    partOne()
    partTwo()
}

private fun partOne() {
    println(
        input()
            .map(Seat::id)
            .maxOrNull()
    )
}

private val ROW_RANGE = 0..127
private val COL_RANGE = 0..7

private fun partTwo() {
    fun findVacantSeatId(): Int? {
        val occupiedSeatIds = input().map(Seat::id).toSet()
        for (row in ROW_RANGE)
            for (col in COL_RANGE) {
                val seatId = seatId(row, col)
                if (seatId !in occupiedSeatIds && (seatId - 1) in occupiedSeatIds && (seatId + 1) in occupiedSeatIds)
                    return seatId
            }
        return null
    }
    println(findVacantSeatId())
}

private fun input() =
    Path.of("input", "day05", "input.txt")
        .linesFromResource()
        .asSequence()
        .map(String::toSeat)

private data class Seat(
    val row: Int,
    val col: Int,
) {
    val id by lazy { seatId(row, col) }
}

private fun seatId(row: Int, col: Int): Int = row * 8 + col

private fun String.toSeat(): Seat {
    if (length != 10) throw IllegalArgumentException("Wrong format: $this")
    return Seat(
        substring(0..6).toRow(),
        substring(7..9).toCol()
    )
}

private fun String.toRow(): Int =
    this
        .replace("F", "0")
        .replace("B", "1")
        .toInt(2)

private fun String.toCol(): Int =
    this
        .replace("L", "0")
        .replace("R", "1")
        .toInt(2)
