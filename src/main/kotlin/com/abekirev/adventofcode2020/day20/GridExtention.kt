package com.abekirev.adventofcode2020.day20

import com.abekirev.adventofcode2020.day20.Direction.DOWN
import com.abekirev.adventofcode2020.day20.Direction.LEFT
import com.abekirev.adventofcode2020.day20.Direction.RIGHT
import com.abekirev.adventofcode2020.day20.Direction.UP
import com.abekirev.adventofcode2020.grid.Grid

fun <T> Grid<T>.sideAt(direction: Direction): GridSide<T> = when (direction) {
    UP -> (0 until size.colCount).map { col -> this[0, col] }
    DOWN -> (0 until size.colCount).map { col -> this[size.rowCount - 1, col] }
    RIGHT -> (0 until size.rowCount).map { row -> this[row, size.colCount - 1] }
    LEFT -> (0 until size.rowCount).map { row -> this[row, 0] }
}

fun Grid<Surface>.println() = println(asString())

fun Grid<Surface>.asString() = asString(Surface::toChar)
