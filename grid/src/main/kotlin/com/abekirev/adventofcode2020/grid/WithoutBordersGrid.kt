package com.abekirev.adventofcode2020.grid

class WithoutBordersGrid<T>(
    private val grid: Grid<T>,
    private val borderWidth: Int,
) : Grid<T> {
    override val size: Size by lazy { Size(grid.size.rowCount - borderWidth * 2, grid.size.colCount - borderWidth * 2) }
    private val offset: Position by lazy { Position(borderWidth, borderWidth) }

    override fun get(row: Int, col: Int): T =
        get(Position(row, col))

    override fun get(pos: Position): T =
        if (pos !in size) throw IndexOutOfBoundsException("$pos is not within $size")
        else grid[pos + offset]
}

fun <T> Grid<T>.withoutBorders(borderWidth: Int) = WithoutBordersGrid(this, borderWidth)
