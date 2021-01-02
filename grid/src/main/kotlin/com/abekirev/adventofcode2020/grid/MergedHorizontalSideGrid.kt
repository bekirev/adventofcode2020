package com.abekirev.adventofcode2020.grid

class MergedHorizontalSideGrid<T> (
    private val topGrid: Grid<T>,
    private val bottomGrid: Grid<T>,
) : Grid<T> {
    init {
        check(topGrid.size.colCount == bottomGrid.size.colCount) {
            "Grids should have the same number of rows"
        }
    }

    override val size: Size by lazy { Size(topGrid.size.rowCount + bottomGrid.size.rowCount, topGrid.size.colCount) }

    override fun get(row: Int, col: Int): T = get(Position(row, col))

    override fun get(pos: Position): T = when {
        pos.row in topGrid.size.rowRange -> topGrid[pos]
        else -> bottomGrid[pos.copy(row = pos.row - topGrid.size.rowCount)]
    }
}

fun <T> Grid<T>.mergedHorizontalSideWith(other: Grid<T>) = MergedHorizontalSideGrid(this, other)
