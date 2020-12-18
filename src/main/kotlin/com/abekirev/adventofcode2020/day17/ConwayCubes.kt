package com.abekirev.adventofcode2020.day17

import com.abekirev.adventofcode2020.day17.CubeState.ACTIVE
import com.abekirev.adventofcode2020.day17.CubeState.INACTIVE
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

private fun partOne() =
    println(
        activeCubesAfterSixTicks { x, y ->
            Position3D(x, y, z = 0)
        }
    )

private fun partTwo() =
    println(
        activeCubesAfterSixTicks { x, y ->
            Position4D(x, y, z = 0, w = 0)
        }
    )

private fun <T : Neighborhood<T>> activeCubesAfterSixTicks(cubeFunc: (x: Int, y: Int) -> T): Int {
    var conwayCubesPocketDimension = Path.of("input", "day17", "input.txt").useLinesFromResource { lines ->
        ConwayCubesPocketDimension(
            lines
                .flatMapIndexed { row, line ->
                    line.asSequence().mapIndexed { col, char ->
                        cubeFunc(col, row) to char.toCubeState()
                    }
                }
                .filter { it.second == ACTIVE }
                .map { it.first }
        )
    }
    repeat(6) {
        conwayCubesPocketDimension = conwayCubesPocketDimension.tick()
    }
    return conwayCubesPocketDimension.activeCubes().count()
}

private class ConwayCubesPocketDimension<T : Neighborhood<T>> private constructor(
    private val activeCubes: MutableSet<T>,
) {
    constructor(
        activeCubes: Sequence<T>,
    ) : this(
        hashSetOf<T>().apply {
            addAll(activeCubes)
        }
    )

    private val inactiveCubes: Sequence<T>
        get() =
            activeCubes
                .asSequence()
                .flatMap { it.neighborhood() }
                .filterNot(activeCubes::contains)
                .distinct()

    fun tick(): ConwayCubesPocketDimension<T> {
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

    private fun T.activeNeighborsCount(): Int =
        neighborhood().count(activeCubes::contains)

    fun activeCubes(): Sequence<T> =
        activeCubes.asSequence()
}

private interface Neighborhood<T> {
    fun neighborhood(): Sequence<T>
}

private data class Position3D(
    val x: Int,
    val y: Int,
    val z: Int,
) : Neighborhood<Position3D> {
    companion object {
        private val ZERO = Position3D(0, 0, 0)
    }

    override fun neighborhood(): Sequence<Position3D> = sequence {
        for (x in -1..1)
            for (y in -1..1)
                for (z in -1..1)
                    yield(Position3D(x, y, z))
    }
        .filterNot(ZERO::equals)
        .map { deltaPos -> this + deltaPos }

    private operator fun Position3D.plus(other: Position3D): Position3D =
        Position3D(
            x + other.x,
            y + other.y,
            z + other.z,
        )
}

private data class Position4D(
    val x: Int,
    val y: Int,
    val z: Int,
    val w: Int,
) : Neighborhood<Position4D> {
    companion object {
        private val ZERO = Position4D(0, 0, 0, 0)
    }

    override fun neighborhood(): Sequence<Position4D> = sequence {
        for (x in -1..1)
            for (y in -1..1)
                for (z in -1..1)
                    for (w in -1..1)
                        yield(Position4D(x, y, z, w))
    }
        .filterNot(ZERO::equals)
        .map { deltaPos -> this + deltaPos }

    private operator fun Position4D.plus(other: Position4D): Position4D =
        Position4D(
            x + other.x,
            y + other.y,
            z + other.z,
            w + other.w,
        )
}

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