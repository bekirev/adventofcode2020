package com.abekirev.adventofcode2020.util.grid

interface Grid<T> {
    val size: Size
    operator fun get(row: Int, col: Int): T
    operator fun get(pos: Position): T
    fun set(updatedCells: Map<Position, T>): Grid<T>
}

data class Position(
    val row: Int,
    val col: Int,
)
