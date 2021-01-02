package com.abekirev.adventofcode2020.grid

class RotatedRightAngleCounterclockwiseGrid<T> private constructor(
    private val grid: Grid<T>,
) : Grid<T> by grid {
    override val size: Size by lazy { Size(rowCount = grid.size.colCount, colCount = grid.size.rowCount) }

    companion object {
        fun <T> of(grid: Grid<T>): RotatedRightAngleCounterclockwiseGrid<T> =
            RotatedRightAngleCounterclockwiseGrid(
                TransformGrid(grid) { (row, col) ->
                    Position(col, grid.size.colCount - row - 1)
                }
            )
    }
}

fun <T> Grid<T>.rotatedRightAngleCounterclockwise(): Grid<T> = RotatedRightAngleCounterclockwiseGrid.of(this)