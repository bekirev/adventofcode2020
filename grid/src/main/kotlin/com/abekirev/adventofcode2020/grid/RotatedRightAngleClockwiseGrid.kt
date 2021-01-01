package com.abekirev.adventofcode2020.grid

class RotatedRightAngleClockwiseGrid<T> private constructor(
    private val grid: Grid<T>,
) : Grid<T> by grid {
    companion object {
        fun <T> of(grid: Grid<T>): RotatedRightAngleClockwiseGrid<T> =
            RotatedRightAngleClockwiseGrid(
                TransformGrid(grid) { (row, col) ->
                    Position(grid.size.rowCount - col - 1, row)
                }
            )
    }
}

fun <T> Grid<T>.rotatedRightAngleClockwise(): Grid<T> = RotatedRightAngleClockwiseGrid.of(this)