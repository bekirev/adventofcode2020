package com.abekirev.adventofcode2020.grid

data class Size(
    val rowCount: Int,
    val colCount: Int,
) {
    val rowRange by lazy { 0 until rowCount }
    val colRange by lazy { 0 until colCount }
}

fun Size.positions() = sequence {
    for (row in rowRange)
        for (col in colRange)
            yield(Position(row, col))
}

operator fun Size.contains(pos: Position): Boolean =
    pos.col in colRange && pos.row in rowRange
