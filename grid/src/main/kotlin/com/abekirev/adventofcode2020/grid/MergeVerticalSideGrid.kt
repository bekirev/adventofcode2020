package com.abekirev.adventofcode2020.grid

class MergeVerticalSideGrid<T>(
    private val leftGrid: Grid<T>,
    private val rightGrid: Grid<T>,
) : Grid<T> {
    init {
        check(leftGrid.size.rowCount == rightGrid.size.rowCount) {
            "Grids should have the same number of rows"
        }
    }

    override val size: Size by lazy { Size(leftGrid.size.rowCount, leftGrid.size.colCount + rightGrid.size.colCount) }

    override fun get(row: Int, col: Int): T = get(Position(row, col))

    override fun get(pos: Position): T = when {
        pos.col in leftGrid.size.colRange -> leftGrid[pos]
        else -> rightGrid[pos.copy(col = pos.col - leftGrid.size.colCount)]
    }
}

fun <T> Grid<T>.mergedVerticalSideWith(other: Grid<T>) = MergeVerticalSideGrid(this, other)
