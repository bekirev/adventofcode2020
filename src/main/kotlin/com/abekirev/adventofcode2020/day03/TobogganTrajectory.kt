package com.abekirev.adventofcode2020.day03

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

private val INPUT_PATH = Path.of("input", "day03", "input.txt")

private fun partOne() =
    println(
        treeHits(Direction(1, 3))
    )

private fun partTwo() =
    println(
        treeHits(Direction(1, 1)) *
            treeHits(Direction(1, 3)) *
            treeHits(Direction(1, 5)) *
            treeHits(Direction(1, 7)) *
            treeHits(Direction(2, 1))
    )

private fun treeHits(direction: Direction): Long =
    INPUT_PATH.useLinesFromResource { lines ->
        lines
            .slope()
            .mapIndexed { index, row ->
                if (index % direction.down == 0) {
                    row[index / direction.down * direction.right % row.size]
                } else {
                    null
                }
            }
            .count(MapTile.TREE::equals)
            .toLong()
    }

private typealias Slope = Sequence<List<MapTile>>

private fun Sequence<String>.slope(): Slope = map { line ->
    line.map(Char::toMapTile)
}


private enum class MapTile {
    OPEN_SQUARE,
    TREE,
    ;
}

private fun Char.toMapTile(): MapTile = when (this) {
    '.' -> MapTile.OPEN_SQUARE
    '#' -> MapTile.TREE
    else -> throw IllegalArgumentException("Unexpected character $this")
}

private data class Direction(
    val down: Int,
    val right: Int,
)
