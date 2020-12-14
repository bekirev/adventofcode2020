package com.abekirev.adventofcode2020.grid

data class Size(
    val rowCount: Int,
    val colCount: Int,
)

fun Size.allPositions() = sequence {
    for (row in 0 until rowCount)
        for (col in 0 until colCount)
            yield(Position(row, col))
}

operator fun Size.contains(pos: Position): Boolean =
    pos.col in 0 until colCount && pos.row in 0 until rowCount