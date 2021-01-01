package com.abekirev.adventofcode2020.day20

import com.abekirev.adventofcode2020.grid.Grid

typealias TileId = Int
typealias TileSide = GridSide<Surface>
typealias GridSide<T> = List<T>

class Tile(
    val id: TileId,
    private val grid: Grid<Surface>,
) : Grid<Surface> by grid {
    override fun asString(toChar: Surface.() -> Char): String =
        sequenceOf(
            "Tile $id:",
            grid.asString(toChar),
        ).joinToString("\n", postfix = "\n")
}
