package com.abekirev.adventofcode2020.day24

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

val tilesFlipper = Path.of("input", "day24", "input.txt").useLinesFromResource { lines ->
    lines
        .map(String::parseDirections)
        .map(Sequence<HexagonalDirection>::toPosition)
        .fold(TilesFlipper()) { counter, position ->
            counter.flip(position)
            counter
        }
}

private fun partOne() {
    println(tilesFlipper.blackTilesCount)
}

private fun partTwo() {
    repeat(100) {
        tilesFlipper.flip()
    }
    println(tilesFlipper.blackTilesCount)
}

class TilesFlipper {
    private val blackTiles: MutableSet<HexagonalPosition> = HashSet()

    fun flip(hexagonalPosition: HexagonalPosition) {
        if (!blackTiles.add(hexagonalPosition)) {
            blackTiles.remove(hexagonalPosition)
        }
    }

    fun flip() {
        val nextBlackTiles = sequenceOf(
            blackTiles.asSequence()
                .map(HexagonalPosition::neighbors)
                .flatMap(Set<HexagonalPosition>::asSequence)
                .filterNot(blackTiles::contains)
                .filter { whiteTilePosition ->
                    whiteTilePosition.blackNeighborsTilesCount() == 2
                },
            blackTiles.asSequence()
                .filter { blackTilePosition ->
                    val blackNeighborsTilesCount = blackTilePosition.blackNeighborsTilesCount()
                    !(blackNeighborsTilesCount == 0 || blackNeighborsTilesCount > 2)
                }
        ).flatten().toSet()
        blackTiles.clear()
        blackTiles.addAll(nextBlackTiles)
    }

    val blackTilesCount: Int by blackTiles::size

    private fun HexagonalPosition.blackNeighborsTilesCount(): Int =
        neighbors.count(blackTiles::contains)
}

fun String.parseDirections(): Sequence<HexagonalDirection> = sequence {
    var prev: Char? = null
    for (char in this@parseDirections.asSequence()) {
        when (prev) {
            is Char -> {
                when ("$prev$char") {
                    "se" -> yield(HexagonalDirection.SOUTH_EAST)
                    "sw" -> yield(HexagonalDirection.SOUTH_WEST)
                    "nw" -> yield(HexagonalDirection.NORTH_WEST)
                    "ne" -> yield(HexagonalDirection.NORTH_EAST)
                    else -> throw IllegalStateException()
                }
                prev = null
            }
            else -> when (char) {
                'e' -> yield(HexagonalDirection.EAST)
                'w' -> yield(HexagonalDirection.WEST)
                else -> prev = char
            }
        }
    }
}

fun Sequence<HexagonalDirection>.toPosition(start: HexagonalPosition = HexagonalPosition.ZERO): HexagonalPosition =
    fold(start, HexagonalPosition::move)

data class HexagonalPosition(
    val east: Int,
    val northEast: Int,
) {
    companion object {
        val ZERO = HexagonalPosition(0, 0)
    }

    val neighbors: Set<HexagonalPosition> by lazy {
        HexagonalDirection.values().mapTo(mutableSetOf()) { move(it) }
    }
}

fun HexagonalPosition.move(direction: HexagonalDirection, tilesCount: Int = 1): HexagonalPosition =
    when (direction) {
        HexagonalDirection.EAST -> copy(east = east + tilesCount)
        HexagonalDirection.WEST -> copy(east = east - tilesCount)
        HexagonalDirection.NORTH_EAST -> copy(northEast = northEast + tilesCount)
        HexagonalDirection.SOUTH_WEST -> copy(northEast = northEast - tilesCount)
        HexagonalDirection.NORTH_WEST -> copy(east = east - tilesCount, northEast = northEast + tilesCount)
        HexagonalDirection.SOUTH_EAST -> copy(east = east + tilesCount, northEast = northEast - tilesCount)
    }

enum class HexagonalDirection {
    EAST,
    WEST,
    NORTH_EAST,
    SOUTH_WEST,
    NORTH_WEST,
    SOUTH_EAST,
    ;
}
