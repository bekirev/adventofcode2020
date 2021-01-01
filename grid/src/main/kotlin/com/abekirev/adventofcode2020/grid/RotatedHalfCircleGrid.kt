package com.abekirev.adventofcode2020.grid

class RotatedHalfCircleGrid<T> private constructor(
    private val grid: Grid<T>,
) : Grid<T> by grid {
    companion object {
        fun <T> of(grid: Grid<T>): RotatedHalfCircleGrid<T> =
            RotatedHalfCircleGrid(
                TransformGrid(grid) { (row, col) ->
                    Position(grid.size.rowCount - row - 1, grid.size.colCount - col - 1)
                }
            )
    }
}

fun <T> Grid<T>.rotatedHalfCircled(): Grid<T> = RotatedHalfCircleGrid.of(this)
