package com.abekirev.adventofcode2020.util.grid

interface Grid<T : Any, G : Grid<T, G>> {
    val size: Size
    operator fun get(row: Int, col: Int): T
    operator fun get(pos: Position): T
    fun set(updatedCells: Map<Position, T>): G
}

data class Position(
    val row: Int,
    val col: Int,
)
