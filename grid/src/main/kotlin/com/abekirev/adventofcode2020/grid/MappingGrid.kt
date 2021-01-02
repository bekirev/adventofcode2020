package com.abekirev.adventofcode2020.grid

class MappingGrid<T, R>(
    private val grid: Grid<T>,
    private val mapping: (Pair<Position, T>) -> R,
) : Grid<R> {
    override val size: Size by grid::size

    override fun get(row: Int, col: Int): R =
        get(Position(row, col))

    override fun get(pos: Position): R =
        mapping(pos to grid[pos])
}

fun <T, R> Grid<T>.mappingGrid(mapping: (Pair<Position, T>) -> R) = MappingGrid(this, mapping)
