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
    partTwo()
}

private fun partOne() =
    println(
        result(ShipFaceDirectionBasedInstructionInterpreter(faceDirection = EAST))
    )

private fun partTwo() =
    println(
        result(WaypointBasedInstructionInterpreter(waypoint = Position(10, 1)))
    )

private fun result(instructionInterpreter: InstructionInterpreter) =
    Path.of("input", "day12", "input.txt").useLinesFromResource { lines ->
        lines
            .map(String::toNavigationInstruction)
            .fold(Ship(Position.ZERO, instructionInterpreter)) { ship, instruction ->
                ship.executeInstruction(instruction)
                ship
            }
            .position
            .manhattanDistance()
    }

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

private class Ship(
    position: Position,
    private val instructionInterpreter: InstructionInterpreter,
) {
    var position: Position = position
        private set

    fun executeInstruction(instruction: NavigationInstruction) {
        val positionToMove = instructionInterpreter.moveTo(position, instruction)
        if (positionToMove != null) {
            position = positionToMove
        }
    }
}

private interface InstructionInterpreter {
    fun moveTo(position: Position, instruction: NavigationInstruction): Position?
}

private const val RIGHT_ANGLE = 90

private class ShipFaceDirectionBasedInstructionInterpreter(
    private var faceDirection: MoveDirection,
) : InstructionInterpreter {
    override fun moveTo(position: Position, instruction: NavigationInstruction): Position? = when (instruction) {
        is GeoDirectionMoveNavigationInstruction -> position.move(
                instruction.moveDirection,
                instruction.value
            )
        is TurnNavigationInstruction -> {
            faceDirection = turn(instruction.turnDirection, instruction.degrees, faceDirection)
            null
        }
        is MoveForwardNavigationInstruction -> position.move(faceDirection, instruction.value)
    }

    companion object {
        private tailrec fun turn(
            turnDirection: TurnDirection,
            degrees: Int,
            faceDirection: MoveDirection,
        ): MoveDirection = when {
            degrees < RIGHT_ANGLE -> faceDirection
            else -> turn(turnDirection, degrees - RIGHT_ANGLE, faceDirection.turn(turnDirection))
        }

        private fun MoveDirection.turn(turnDirection: TurnDirection): MoveDirection = when (this) {
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
}

private class WaypointBasedInstructionInterpreter(
    private var waypoint: Position,
) : InstructionInterpreter {
    override fun moveTo(position: Position, instruction: NavigationInstruction): Position? = when (instruction) {
        is GeoDirectionMoveNavigationInstruction -> {
            waypoint = waypoint.move(instruction.moveDirection, instruction.value)
            null
        }
        is TurnNavigationInstruction -> {
            waypoint = turn(waypoint, instruction.turnDirection, instruction.degrees)
            null
        }
        is MoveForwardNavigationInstruction -> position + waypoint * instruction.value
    }

    companion object {
        private tailrec fun turn(
            position: Position,
            degrees: Int,
            rightAngleTurn: (Position) -> Position,
        ): Position = when {
            degrees < RIGHT_ANGLE -> position
            else -> turn(rightAngleTurn(position), degrees - RIGHT_ANGLE, rightAngleTurn)
        }

        private fun turn(position: Position, turnDirection: TurnDirection, degrees: Int): Position =
            turn(position, degrees,) { (east, north) ->
                when (turnDirection) {
                    LEFT -> Position(east = -north, north = east)
                    RIGHT -> Position(east = north, north = -east)
                }
            }
    }
}

private data class Position(
    val east: Int,
    val north: Int,
) {
    companion object {
        val ZERO = Position(0, 0)
    }
}

private fun Position.move(moveDirection: MoveDirection, value: Int): Position = when (moveDirection) {
    NORTH -> copy(north = north + value)
    SOUTH -> copy(north = north - value)
    EAST -> copy(east = east + value)
    WEST -> copy(east = east - value)
}

private operator fun Position.times(value: Int): Position =
    Position(east * value, north * value)

private operator fun Position.plus(other: Position): Position =
    Position(east + other.east, north + other.north)

private fun Position.manhattanDistance() =
    east.absoluteValue + north.absoluteValue

private enum class MoveDirection {
    NORTH,
    SOUTH,
    EAST,
    WEST,
    ;
}

private enum class TurnDirection {
    LEFT,
    RIGHT,
    ;
}

private sealed class NavigationInstruction
private data class GeoDirectionMoveNavigationInstruction(
    val moveDirection: MoveDirection,
    val value: Int,
) : NavigationInstruction()

private data class TurnNavigationInstruction(
    val turnDirection: TurnDirection,
    val degrees: Int,
) : NavigationInstruction()

private data class MoveForwardNavigationInstruction(
    val value: Int,
) : NavigationInstruction()
