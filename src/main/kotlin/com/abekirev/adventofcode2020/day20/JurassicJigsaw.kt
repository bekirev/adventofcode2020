package com.abekirev.adventofcode2020.day20

import com.abekirev.adventofcode2020.day20.Direction.DOWN
import com.abekirev.adventofcode2020.day20.Direction.LEFT
import com.abekirev.adventofcode2020.day20.Direction.RIGHT
import com.abekirev.adventofcode2020.day20.Direction.UP
import com.abekirev.adventofcode2020.day20.Surface.TYPE_1
import com.abekirev.adventofcode2020.day20.Surface.TYPE_2
import com.abekirev.adventofcode2020.grid.Grid
import com.abekirev.adventofcode2020.grid.MutableMapGrid
import com.abekirev.adventofcode2020.grid.Position
import com.abekirev.adventofcode2020.grid.Size
import com.abekirev.adventofcode2020.grid.mirroredHorizontalLine
import com.abekirev.adventofcode2020.grid.mirroredVerticalLine
import com.abekirev.adventofcode2020.grid.rotatedHalfCircled
import com.abekirev.adventofcode2020.grid.rotatedRightAngleClockwise
import com.abekirev.adventofcode2020.grid.rotatedRightAngleCounterclockwise
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
}

private fun partOne() {
    val tiles = Path.of("input", "day20", "input.txt").useLinesFromResource { lines ->
        val possibleTransformedGrids: Collection<(Grid<Surface>) -> Grid<Surface>> = listOf(
            { it.rotatedHalfCircled() },
            { it.rotatedRightAngleClockwise() },
            { it.rotatedRightAngleCounterclockwise() },
            { it.mirroredVerticalLine() },
            { it.mirroredHorizontalLine() },
            { it.rotatedRightAngleClockwise().mirroredHorizontalLine() },
            { it.rotatedRightAngleClockwise().mirroredVerticalLine() },
        )
        lines
            .tiles()
            .flatMap { (id, grid) ->
                sequenceOf(
                    sequenceOf(grid),
                    possibleTransformedGrids.asSequence().map { transformedGrid -> transformedGrid(grid) }
                ).flatten()
                    .map { Tile(id, it) }
            }
            .toList()
    }
    val tileSideRegistry = TileSideRegistry(tiles.asSequence())
    val startTile = tiles.first()
    val topTile = tileSideRegistry.tilesSequence(startTile, UP).last()
    val topLeftCornerTile = tileSideRegistry.tilesSequence(topTile, LEFT).last()
    val topRightCornerTile = tileSideRegistry.tilesSequence(topTile, RIGHT).last()
    val bottomTile = tileSideRegistry.tilesSequence(startTile, DOWN).last()
    val bottomLeftCornerTile = tileSideRegistry.tilesSequence(bottomTile, LEFT).last()
    val bottomRightCornerTile = tileSideRegistry.tilesSequence(bottomTile, RIGHT).last()
    println(topLeftCornerTile.id.toLong() * topRightCornerTile.id.toLong() * bottomLeftCornerTile.id.toLong() * bottomRightCornerTile.id.toLong())
}

fun Sequence<String>.tiles(): Sequence<Pair<TileId, Grid<Surface>>> = sequence {
    var tileId: TileId? = null
    var tileGridMap = mutableMapOf<Position, Surface>()
    var rowCount = 0
    var colCount = 0
    fun tilePair(): Pair<TileId, Grid<Surface>> =
        Pair(
            tileId!!,
            MutableMapGrid(
                Size(
                    rowCount,
                    colCount,
                ),
            ) { row, col ->
                tileGridMap[Position(row, col)]!!
            }
        )

    for (line in this@tiles) {
        when {
            line.contains("Tile") -> {
                tileId = line.substringAfter(" ").substringBefore(":").toInt()
            }
            line.isBlank() -> {
                yield(tilePair())
                tileGridMap = mutableMapOf()
                tileId = null
                rowCount = 0
                colCount = 0
            }
            else -> {
                line
                    .map(Char::toSurface)
                    .forEachIndexed { index, surface ->
                        tileGridMap[Position(rowCount, index)] = surface
                    }
                colCount = line.length
                ++rowCount
            }
        }
    }
    if (tileGridMap.isNotEmpty()) {
        yield(tilePair())
    }
}

enum class Surface {
    TYPE_1,
    TYPE_2,
    ;
}

fun Char.toSurface(): Surface = when (this) {
    '#' -> TYPE_1
    '.' -> TYPE_2
    else -> throw IllegalArgumentException("Unknown surface type")
}

fun Surface.toChar(): Char = when (this) {
    TYPE_1 -> '#'
    TYPE_2 -> '.'
}
