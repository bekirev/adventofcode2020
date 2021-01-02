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
import com.abekirev.adventofcode2020.grid.mergedHorizontalSideWith
import com.abekirev.adventofcode2020.grid.mergedVerticalSideWith
import com.abekirev.adventofcode2020.grid.mirroredHorizontalLine
import com.abekirev.adventofcode2020.grid.mirroredVerticalLine
import com.abekirev.adventofcode2020.grid.plus
import com.abekirev.adventofcode2020.grid.positionWithValues
import com.abekirev.adventofcode2020.grid.positions
import com.abekirev.adventofcode2020.grid.rotatedHalfCircled
import com.abekirev.adventofcode2020.grid.rotatedRightAngleClockwise
import com.abekirev.adventofcode2020.grid.rotatedRightAngleCounterclockwise
import com.abekirev.adventofcode2020.grid.withoutBorders
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

fun <T> possibleTransformations(): Sequence<(Grid<T>) -> Grid<T>> = sequenceOf(
    { it.rotatedHalfCircled() },
    { it.rotatedRightAngleClockwise() },
    { it.rotatedRightAngleCounterclockwise() },
    { it.mirroredVerticalLine() },
    { it.mirroredHorizontalLine() },
    { it.rotatedRightAngleClockwise().mirroredHorizontalLine() },
    { it.rotatedRightAngleClockwise().mirroredVerticalLine() },
)

private fun partOne() {
    val tiles = tiles()
    val tileSideRegistry = TileSideRegistry(tiles.asSequence())
    val startTile = tiles.first()
    val topTile = tileSideRegistry.tilesSequence(startTile, UP).last()
    val topLeftCornerTile = tileSideRegistry.tilesSequence(topTile, LEFT).last()
    val topRightCornerTile = tileSideRegistry.tilesSequence(topTile, RIGHT).last()
    val bottomTile = tileSideRegistry.tilesSequence(startTile, DOWN).last()
    val bottomLeftCornerTile = tileSideRegistry.tilesSequence(bottomTile, LEFT).last()
    val bottomRightCornerTile = tileSideRegistry.tilesSequence(bottomTile, RIGHT).last()
    println(
        sequenceOf(
            topLeftCornerTile.id,
            topRightCornerTile.id,
            bottomLeftCornerTile.id,
            bottomRightCornerTile.id,
        )
            .map(Int::toLong)
            .reduce(Long::times)
    )
}

private fun partTwo() {
    val seaMonsterPatternGrid = seaMonsterPatternGrid()
    val scanners: Sequence<MonsterScanner> = sequenceOf(
        sequenceOf(seaMonsterPatternGrid),
        possibleTransformations<CellPattern<Surface>>().map { it(seaMonsterPatternGrid) }
    ).flatten()
        .map(::MonsterScanner)
    val grid = entireImage()
    val firstTypeCount = grid.positionWithValues().map { it.second }.count(TYPE_1::equals)
    val monsterCellCount = scanners.flatMap { it.scan(grid) }.distinct().count()
    println(firstTypeCount - monsterCellCount)
}

fun seaMonsterPatternGrid(): Grid<CellPattern<Surface>> =
    sequenceOf(
        "                  # ",
        "#    ##    ##    ###",
        " #  #  #  #  #  #   ",
    ).toGrid(Char::toGridCellPattern)

fun entireImage(): Grid<Surface> {
    val tiles = tiles()
    val tileSideRegistry = TileSideRegistry(tiles.asSequence())
    val startTile = tiles.first()
    val topTile = tileSideRegistry.tilesSequence(startTile, UP).last()
    val topLeftCornerTile = tileSideRegistry.tilesSequence(topTile, LEFT).last()
    return tileSideRegistry.tilesSequence(topLeftCornerTile, DOWN).map { tile ->
        tileSideRegistry.tilesSequence(tile, RIGHT)
            .map { it.withoutBorders(1) }
            .reduce(Grid<Surface>::mergedVerticalSideWith)
    }
        .reduce(Grid<Surface>::mergedHorizontalSideWith)
}

fun tiles(): List<Tile> =
    Path.of("input", "day20", "input.txt").useLinesFromResource { lines ->
        lines
            .tiles()
            .flatMap { (id, grid) ->
                sequenceOf(
                    sequenceOf(grid),
                    possibleTransformations<Surface>().map { transformedGrid -> transformedGrid(grid) }
                ).flatten()
                    .map { Tile(id, it) }
            }
            .toList()
    }

class MonsterScanner(
    private val monsterPattern: Grid<CellPattern<Surface>>,
) {
    fun scan(grid: Grid<Surface>): Sequence<Position> {
        val effectiveSize = Size(
            grid.size.rowCount - monsterPattern.size.rowCount + 1,
            grid.size.colCount - monsterPattern.size.colCount + 1
        )
        return effectiveSize.positions()
            .filter { topLeftCorner -> matches(grid, topLeftCorner) }
            .flatMap { topLeftCorner -> monsterCells(topLeftCorner)}
    }

    private fun matches(
        grid: Grid<Surface>,
        topLeftCorner: Position,
    ) =
        monsterPattern.positionWithValues()
            .all { (position, gridCellPattern) ->
                when (gridCellPattern) {
                    AnyValue -> true
                    is ExactValueCellPattern<Surface> -> try {
                        grid[topLeftCorner + position] matches gridCellPattern
                    } catch (e: IndexOutOfBoundsException) {
                        false
                    }
                }
            }

    private fun monsterCells(
        topLeftCorner: Position,
    ): Sequence<Position> =
        monsterPattern.positionWithValues()
            .mapNotNull { (position, value) ->
                when (value) {
                    AnyValue -> null
                    is ExactValueCellPattern -> when (value.value) {
                        TYPE_1 -> position + topLeftCorner
                        else -> null
                    }
                }
            }
}

fun <T : Any> Sequence<String>.toGrid(toElem: Char.() -> T): Grid<T> {
    var row = -1
    var maxCol = -1
    val map = mutableMapOf<Position, T>()
    for (line in this) {
        ++row
        line.forEachIndexed { col, char ->
            map[Position(row, col)] = char.toElem()
            if (col > maxCol) maxCol = col
        }
    }
    return MutableMapGrid(Size(row + 1, maxCol + 1)) { pos ->
        map[pos]!!
    }
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

enum class Surface(val char: Char) {
    TYPE_1('#'),
    TYPE_2('.'),
    ;
}

fun Char.toSurface(): Surface = when (this) {
    TYPE_1.char -> TYPE_1
    TYPE_2.char -> TYPE_2
    else -> throw IllegalArgumentException("Unknown surface type")
}

fun Surface.toChar(): Char = char

sealed class CellPattern<in T> {
    abstract fun check(value: T): Boolean
}

infix fun <T> T.matches(cellPattern: CellPattern<T>): Boolean = cellPattern.check(this)

object AnyValue : CellPattern<Any?>() {
    override fun check(value: Any?): Boolean = true
}

data class ExactValueCellPattern<T>(val value: T) : CellPattern<T>() {
    override fun check(value: T): Boolean = this.value == value
}

fun Char.toGridCellPattern(): CellPattern<Surface> = when (this) {
    TYPE_1.char -> ExactValueCellPattern(TYPE_1)
    TYPE_2.char -> ExactValueCellPattern(TYPE_2)
    ' ' -> AnyValue
    else -> throw IllegalArgumentException("Unknown surface type")
}
