package com.abekirev.adventofcode2020.grid

class TransformGrid<T>(
    private val grid: Grid<T>,
    private val posTransFun: (Position) -> Position,
) : Grid<T> {
    override val size: Size by grid::size

    override fun get(row: Int, col: Int): T =
        grid[posTransFun(Position(row, col))]

    override fun get(pos: Position): T =
        grid[posTransFun(pos)]
}
