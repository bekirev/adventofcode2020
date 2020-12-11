package com.abekirev.adventofcode2020.day11

import com.abekirev.adventofcode2020.day11.Cell.FLOOR
import com.abekirev.adventofcode2020.day11.Cell.OCCUPIED_SEAT
import com.abekirev.adventofcode2020.day11.Cell.EMPTY_SEAT
import com.abekirev.adventofcode2020.util.grid.Grid
import com.abekirev.adventofcode2020.util.grid.MutableMapGrid
import com.abekirev.adventofcode2020.util.grid.Position
import com.abekirev.adventofcode2020.util.grid.Size
import com.abekirev.adventofcode2020.util.grid.allPositions
import com.abekirev.adventofcode2020.util.grid.contains
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
}

private fun partOne() =
    println(
        Path.of("input", "day11", "input.txt").useLinesFromResource { lines ->
            val linesOfCells = lines
                .map { line -> line.map(Char::toCell) }
                .toList()
            val size = Size(
                rowCount = linesOfCells.size,
                colCount = linesOfCells.first().size
            )
            val finalGrid = SeatingSystem(
                MutableMapGrid(size) { row, col ->
                    linesOfCells[row][col]
                },
                EightNeighborsGridCellCellStateChangeDeterminer()
            ).finalState()
            size
                .allPositions()
                .map(finalGrid::get)
                .count(OCCUPIED_SEAT::equals)
        }
    )

private class SeatingSystem<G : Grid<Cell, G>>(
    private var grid: G,
    private val gridCellStateChangeDeterminer: GridCellStateChangeDeterminer<G>,
) {
    fun finalState(): G {
        val stateChanges: MutableMap<Position, Cell> = mutableMapOf()
        do {
            stateChanges.clear()
            stateChanges.putAll(
                grid.size.allPositions()
                    .mapNotNull { pos ->
                        gridCellStateChangeDeterminer
                            .determineAtPosition(pos, grid)
                            ?.let { stateChange ->
                                pos to stateChange.newState
                            }
                    }
            )
            if (stateChanges.isNotEmpty())
                grid = grid.set(stateChanges)
        } while (stateChanges.isNotEmpty())
        return grid
    }
}

private fun interface GridCellStateChangeDeterminer<G : Grid<Cell, G>> {
    fun determineAtPosition(pos: Position, grid: G): StateChange?
}

private class EightNeighborsGridCellCellStateChangeDeterminer<G : Grid<Cell, G>> : GridCellStateChangeDeterminer<G> {
    companion object {
        fun Position.eightNeighbors() = sequence {
            yield(this@eightNeighbors.plusCol(1))
            yield(this@eightNeighbors.plusCol(-1))
            yield(this@eightNeighbors.plusRow(1))
            yield(this@eightNeighbors.plusRow(-1))
            yield(this@eightNeighbors.plus(1, 1))
            yield(this@eightNeighbors.plus(-1, 1))
            yield(this@eightNeighbors.plus(1, -1))
            yield(this@eightNeighbors.plus(-1, -1))
        }
    }

    override fun determineAtPosition(pos: Position, grid: G): StateChange? {
        val eightNeighbors by lazy { pos.eightNeighbors().filter(grid.size::contains).map(grid::get) }
        return when (grid[pos]) {
            EMPTY_SEAT -> {
                if (!eightNeighbors.any(OCCUPIED_SEAT::equals))
                    StateChange(OCCUPIED_SEAT)
                else
                    null
            }
            OCCUPIED_SEAT -> {
                if (eightNeighbors.count(OCCUPIED_SEAT::equals) >= 4)
                    StateChange(EMPTY_SEAT)
                else
                    null
            }
            else -> null
        }
    }
}

private fun Position.plus(rowValue: Int, colValue: Int): Position =
    copy(row = row + rowValue, col = col + colValue)

private fun Position.plusRow(rowValue: Int): Position =
    copy(row = row + rowValue)

private fun Position.plusCol(colValue: Int): Position =
    copy(col = col + colValue)

private data class StateChange(val newState: Cell)

private enum class Cell {
    FLOOR,
    EMPTY_SEAT,
    OCCUPIED_SEAT,
}

private fun Char.toCell(): Cell = when (this) {
    '.' -> FLOOR
    'L' -> EMPTY_SEAT
    '#' -> OCCUPIED_SEAT
    else -> throw IllegalArgumentException("Unknown cell $this")
}
