package com.abekirev.adventofcode2020.day03

import com.abekirev.adventofcode2020.util.linesFromResource
import java.nio.file.Path
import kotlin.streams.asSequence

fun main() {
    partOne()
    partTwo()
}

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
    slope()
        .mapIndexed { index, row ->
            if (index % direction.down == 0) {
                row[index / direction.down * direction.right % row.size]
            } else {
                null
            }
        }
        .count(MapTile.TREE::equals)
        .toLong()

private typealias Slope = Sequence<List<MapTile>>

private fun slope(): Slope =
    Path.of("input", "day03", "input.txt")
        .linesFromResource()
        .asSequence()
        .map { line -> line.map(Char::toMapTile) }


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
