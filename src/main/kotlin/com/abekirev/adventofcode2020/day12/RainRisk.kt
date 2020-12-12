package com.abekirev.adventofcode2020.day12

import com.abekirev.adventofcode2020.day12.MoveDirection.EAST
import com.abekirev.adventofcode2020.day12.MoveDirection.NORTH
import com.abekirev.adventofcode2020.day12.MoveDirection.SOUTH
import com.abekirev.adventofcode2020.day12.MoveDirection.WEST
import com.abekirev.adventofcode2020.day12.TurnDirection.LEFT
import com.abekirev.adventofcode2020.day12.TurnDirection.RIGHT
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path
import kotlin.math.absoluteValue

fun main() {
    partOne()
}

private fun partOne() =
    println(
        Path.of("input", "day12", "input.txt").useLinesFromResource { lines ->
            lines
                .map(String::toNavigationInstruction)
                .fold(Ship(Position.ZERO, EAST)) { ship, instriuction ->
                    instriuction.moveShip(ship)
                }
                .position
                .manhattanDistance()
        }
    )

private fun String.toNavigationInstruction(): NavigationInstruction {
    val intValue = substring(1).toInt()
    return when (first()) {
        'N' -> GeoDirectionMoveNavigationInstruction(NORTH, intValue)
        'S' -> GeoDirectionMoveNavigationInstruction(SOUTH, intValue)
        'E' -> GeoDirectionMoveNavigationInstruction(EAST, intValue)
        'W' -> GeoDirectionMoveNavigationInstruction(WEST, intValue)
        'L' -> TurnNavigationInstruction(LEFT, intValue)
        'R' -> TurnNavigationInstruction(RIGHT, intValue)
        'F' -> MoveForwardNavigationInstruction(intValue)
        else -> throw IllegalArgumentException("Unknown instruction: $this")
    }
}

private data class Ship(
    val position: Position,
    val moveDirection: MoveDirection,
)

private data class Position(
    val east: Int,
    val north: Int,
) {
    companion object {
        val ZERO = Position(0, 0)
    }
}

private fun Position.manhattanDistance() =
    east.absoluteValue + north.absoluteValue

private enum class MoveDirection {
    NORTH,
    SOUTH,
    EAST,
    WEST,
    ;
}

private interface NavigationInstruction {
    fun moveShip(ship: Ship): Ship
}

private class GeoDirectionMoveNavigationInstruction(
    private val moveDirection: MoveDirection,
    private val value: Int,
) : NavigationInstruction {
    override fun moveShip(ship: Ship): Ship =
        ship.copy(position = when (moveDirection) {
            NORTH -> ship.position.copy(north = ship.position.north + value)
            SOUTH -> ship.position.copy(north = ship.position.north - value)
            EAST -> ship.position.copy(east = ship.position.east + value)
            WEST -> ship.position.copy(east = ship.position.east - value)
        })
}

private enum class TurnDirection {
    LEFT,
    RIGHT,
    ;
}

private class TurnNavigationInstruction(
    private val turnDirection: TurnDirection,
    private val degrees: Int,
) : NavigationInstruction {
    companion object {
        private const val RIGHT_ANGLE = 90

        private tailrec fun turn(
            turnDirection: TurnDirection,
            degrees: Int,
            moveDirection: MoveDirection,
        ): MoveDirection = when {
            degrees < RIGHT_ANGLE -> moveDirection
            else -> turn(turnDirection, degrees - RIGHT_ANGLE, moveDirection.turn(turnDirection))
        }

        private fun MoveDirection.turn(turnDirection: TurnDirection): MoveDirection = when(this) {
            NORTH -> when (turnDirection) {
                LEFT -> WEST
                RIGHT -> EAST
            }
            SOUTH -> when (turnDirection) {
                LEFT -> EAST
                RIGHT -> WEST
            }
            EAST -> when (turnDirection) {
                LEFT -> NORTH
                RIGHT -> SOUTH
            }
            WEST -> when (turnDirection) {
                LEFT -> SOUTH
                RIGHT -> NORTH
            }
        }
    }
    override fun moveShip(ship: Ship): Ship =
        ship.copy(moveDirection = turn(turnDirection, degrees, ship.moveDirection))
}

private class MoveForwardNavigationInstruction(
    private val value: Int,
) : NavigationInstruction {
    override fun moveShip(ship: Ship): Ship =
        ship.copy(
            position = when (ship.moveDirection) {
                NORTH -> ship.position.copy(north = ship.position.north + value)
                SOUTH -> ship.position.copy(north = ship.position.north - value)
                EAST -> ship.position.copy(east = ship.position.east + value)
                WEST -> ship.position.copy(east = ship.position.east - value)
            }
        )
}
