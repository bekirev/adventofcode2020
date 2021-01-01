package com.abekirev.adventofcode2020.grid

class MirroredHorizontalLineGrid<T> private constructor(
    private val grid: Grid<T>,
) : Grid<T> by grid {
    companion object {
        fun <T> of(grid: Grid<T>): MirroredHorizontalLineGrid<T> =
            MirroredHorizontalLineGrid(
                TransformGrid(grid) { (row, col) ->
                    Position(grid.size.rowCount - row - 1, col)
                }
            )
    }
}

fun <T> Grid<T>.mirroredHorizontalLine(): Grid<T> = MirroredHorizontalLineGrid.of(this)
