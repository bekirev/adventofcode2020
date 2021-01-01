package com.abekirev.adventofcode2020.grid

interface Grid<T> {
    val size: Size
    operator fun get(row: Int, col: Int): T
    operator fun get(pos: Position): T
    fun asString(toChar: T.() -> Char): String =
        (0 until size.rowCount).asSequence().map { row ->
            (0 until size.colCount).asSequence().map { col ->
                this[row, col].toChar()
            }.joinToString("")
        }.joinToString("\n")
}

interface MutableGrid<T> : Grid<T> {
    fun set(updatedCells: Map<Position, T>): MutableGrid<T>
}

data class Position(
    val row: Int,
    val col: Int,
)
