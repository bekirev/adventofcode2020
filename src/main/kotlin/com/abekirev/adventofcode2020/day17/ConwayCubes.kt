package com.abekirev.adventofcode2020.day17

import com.abekirev.adventofcode2020.day17.CubeState.ACTIVE
import com.abekirev.adventofcode2020.day17.CubeState.INACTIVE
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
}

private fun partOne() {
    var conwayCubesPocketDimension = Path.of("input", "day17", "input.txt").useLinesFromResource { lines ->
        ConwayCubesPocketDimension(
            lines
                .flatMapIndexed { row, line ->
                    line.asSequence().mapIndexed { col, char ->
                        Position3D(x = row, y = col, z = 0) to char.toCubeState()
                    }
                }
                .filter { it.second == ACTIVE }
                .map { it.first }
        )
    }
    repeat(6) {
        conwayCubesPocketDimension = conwayCubesPocketDimension.tick()
    }
    println(conwayCubesPocketDimension.activeCubes().count())
}

private class ConwayCubesPocketDimension private constructor(
    private val activeCubes: MutableSet<Position3D>,
) {
    constructor(
        activeCubes: Sequence<Position3D>,
    ) : this(
        hashSetOf<Position3D>().apply {
            addAll(activeCubes)
        }
    )

    private val inactiveCubes: Sequence<Position3D>
        get() =
            activeCubes
                .asSequence()
                .flatMap(Position3D::neighbors)
                .filterNot(activeCubes::contains)

    fun tick(): ConwayCubesPocketDimension {
        sequenceOf(
            inactiveCubes.map { position ->
                position to if (position.activeNeighborsCount() == 3) ACTIVE else INACTIVE
            },
            activeCubes.asSequence().map { position ->
                position to if (position.activeNeighborsCount() in 2..3) ACTIVE else INACTIVE
            }
        ).flatten()
            .toList()
            .forEach { (position, cubeState) ->
                when (cubeState) {
                    ACTIVE -> activeCubes::add
                    INACTIVE -> activeCubes::remove
                }(position)
            }
        return this
    }

    private fun Position3D.activeNeighborsCount(): Int =
        neighbors().count(activeCubes::contains)

    fun activeCubes(): Sequence<Position3D> =
        activeCubes.asSequence()
}

private data class Position3D(
    val x: Int,
    val y: Int,
    val z: Int,
) {
    companion object {
        val ZERO = Position3D(0, 0, 0)
    }
}

private operator fun Position3D.plus(other: Position3D): Position3D =
    Position3D(
        x + other.x,
        y + other.y,
        z + other.z,
    )

private fun Position3D.neighbors(): Sequence<Position3D> = sequence {
    for (x in -1..1)
        for (y in -1..1)
            for (z in -1..1)
                yield(Position3D(x, y, z))
}
    .filterNot(Position3D.ZERO::equals)
    .map { deltaPos -> this + deltaPos }

private enum class CubeState {
    ACTIVE,
    INACTIVE,
    ;
}

private fun Char.toCubeState(): CubeState = when (this) {
    '.' -> INACTIVE
    '#' -> ACTIVE
    else -> throw IllegalArgumentException("Unknown cube state $this")
}