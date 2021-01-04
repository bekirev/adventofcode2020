package com.abekirev.adventofcode2020.day24

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
}

private fun partOne() {
    println(
        Path.of("input", "day24", "input.txt").useLinesFromResource { lines ->
            lines
                .map(String::parseDirections)
                .map(Sequence<HexagonalDirection>::toPosition)
                .fold(BlackTilesCounter()) { counter, position ->
                    counter.flip(position)
                    counter
                }.blackTilesCount
        }
    )
}

class BlackTilesCounter {
    private val blackTiles: MutableSet<HexagonalPosition> = HashSet()

    fun flip(hexagonalPosition: HexagonalPosition) {
        if (!blackTiles.add(hexagonalPosition)) {
            blackTiles.remove(hexagonalPosition)
        }
    }

    val blackTilesCount: Int by blackTiles::size
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
