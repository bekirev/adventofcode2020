package com.abekirev.adventofcode2020.day05

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

private val INPUT_PATH = Path.of("input", "day05", "input.txt")

private fun partOne() {
    println(
        INPUT_PATH.useLinesFromResource { lines ->
            lines
                .seats()
                .map(Seat::id)
                .maxOrNull()
        }
    )
}

private val ROW_RANGE = 0..127
private val COL_RANGE = 0..7

private fun partTwo() {
    fun Sequence<Seat>.vacantSeatIdOrNull(): Int? {
        val occupiedSeatIds = map(Seat::id).toSet()
        for (row in ROW_RANGE)
            for (col in COL_RANGE) {
                val seatId = seatId(row, col)
                if (seatId !in occupiedSeatIds && (seatId - 1) in occupiedSeatIds && (seatId + 1) in occupiedSeatIds)
                    return seatId
            }
        return null
    }
    println(
        INPUT_PATH.useLinesFromResource { lines ->
            lines
                .seats()
                .vacantSeatIdOrNull()
        }
    )
}

private fun Sequence<String>.seats() = map(String::toSeat)

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
