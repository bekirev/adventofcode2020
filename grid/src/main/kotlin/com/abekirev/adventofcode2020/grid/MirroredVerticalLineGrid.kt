package com.abekirev.adventofcode2020.grid

class MirroredVerticalLineGrid<T> private constructor(
    private val grid: Grid<T>,
) : Grid<T> by grid {
    companion object {
        fun <T> of(grid: Grid<T>): MirroredVerticalLineGrid<T> =
            MirroredVerticalLineGrid(
                TransformGrid(grid) { (row, col) ->
                    Position(row, grid.size.colCount - col - 1)
                }
            )
    }
}

fun <T> Grid<T>.mirroredVerticalLine(): Grid<T> = MirroredVerticalLineGrid.of(this)
