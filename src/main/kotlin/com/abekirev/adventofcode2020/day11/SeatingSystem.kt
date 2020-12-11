package com.abekirev.adventofcode2020.day11

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
    partTwo()
}

private fun partOne() =
    println(
        occupiedSeatsCountAfterEquilibrium(
            EightNearestNeighborsGridCellCellStateChangeDeterminer(4)
        )
    )

private fun partTwo() =
    println(
        occupiedSeatsCountAfterEquilibrium(
            EightVisibleGridCellCellStateChangeDeterminer(5)
        )
    )

private fun occupiedSeatsCountAfterEquilibrium(gridCellCellStateChangeDeterminer: GridCellStateChangeDeterminer<MutableMapGrid<Cell>>) =
    Path.of("input", "day11", "input.txt").useLinesFromResource { lines ->
        val grid = lines.toGrid()
        val finalGrid = SeatingSystem(
            grid,
            gridCellCellStateChangeDeterminer
        ).equilibriumState()
        finalGrid.size
            .allPositions()
            .map(finalGrid::get)
            .count(OccupiedSeat::equals)
    }

private fun Sequence<String>.toGrid(): MutableMapGrid<Cell> {
    val linesOfCells = map { line -> line.map(Char::toCell) }.toList()
    val size = Size(
        rowCount = linesOfCells.size,
        colCount = linesOfCells.first().size
    )
    return MutableMapGrid(size) { row, col ->
        linesOfCells[row][col]
    }
}

private class SeatingSystem<G : Grid<Cell, G>>(
    private var grid: G,
    private val gridCellStateChangeDeterminer: GridCellStateChangeDeterminer<G>,
) {
    fun equilibriumState(): G {
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

private class VisibleSeatsCheckGridCellStateChangeDeterminer<G : Grid<Cell, G>>(
    private val occupiedSeatsThreshold: Int,
    private val visibleSeatsFindingStrategy: (pos: Position, grid: G) -> Sequence<Seat>,
) : GridCellStateChangeDeterminer<G> {
    override fun determineAtPosition(pos: Position, grid: G): StateChange? {
        val visibleSeats by lazy { visibleSeatsFindingStrategy(pos, grid) }
        return when (grid[pos]) {
            EmptySeat -> {
                if (!visibleSeats.any(OccupiedSeat::equals))
                    StateChange(OccupiedSeat)
                else
                    null
            }
            OccupiedSeat -> {
                if (visibleSeats.count(OccupiedSeat::equals) >= occupiedSeatsThreshold)
                    StateChange(EmptySeat)
                else
                    null
            }
            else -> null
        }
    }
}

private class EightNearestNeighborsGridCellCellStateChangeDeterminer<G : Grid<Cell, G>> private constructor(
    private val visibleSeatsCheckGridCellStateChangeDeterminer: VisibleSeatsCheckGridCellStateChangeDeterminer<G>,
) : GridCellStateChangeDeterminer<G> by visibleSeatsCheckGridCellStateChangeDeterminer {
    constructor(occupiedSeatsThreshold: Int) : this(
        VisibleSeatsCheckGridCellStateChangeDeterminer(occupiedSeatsThreshold, ::visibleSeats)
    )

    companion object {
        private fun <G : Grid<Cell, G>> visibleSeats(pos: Position, grid: G): Sequence<Seat> =
            sequenceOf(
                pos.plusCol(1),
                pos.plusCol(-1),
                pos.plusRow(1),
                pos.plusRow(-1),
                pos.plus(1, 1),
                pos.plus(-1, 1),
                pos.plus(1, -1),
                pos.plus(-1, -1),
            )
                .filter(grid.size::contains)
                .map(grid::get)
                .filterIsInstance<Seat>()
    }
}

private class EightVisibleGridCellCellStateChangeDeterminer<G : Grid<Cell, G>> private constructor(
    private val visibleSeatsCheckGridCellStateChangeDeterminer: VisibleSeatsCheckGridCellStateChangeDeterminer<G>,
) : GridCellStateChangeDeterminer<G> by visibleSeatsCheckGridCellStateChangeDeterminer {
    constructor(occupiedSeatsThreshold: Int) : this(
        VisibleSeatsCheckGridCellStateChangeDeterminer<G>(occupiedSeatsThreshold, ::visibleSeats)
    )

    companion object {
        private fun <G : Grid<Cell, G>> visibleSeats(pos: Position, grid: G): Sequence<Seat> {
            fun cellsInDirection(rowValue: Int, colValue: Int) = sequence {
                var curPos = pos
                while (true) {
                    val nextPos = curPos.plus(rowValue, colValue)
                    if (nextPos in grid.size) {
                        yield(nextPos)
                        curPos = nextPos
                    } else {
                        break
                    }
                }
            }.map(grid::get)
            return sequenceOf(
                Direction(0, 1),
                Direction(0, -1),
                Direction(1, 0),
                Direction(-1, 0),
                Direction(1, 1),
                Direction(-1, 1),
                Direction(1, -1),
                Direction(-1, -1),
            )
                .flatMap { (rowValue, colValue) ->
                    sequenceOf(cellsInDirection(rowValue, colValue).filterIsInstance<Seat>().firstOrNull())
                }
                .filterNotNull()
        }
    }
}

private data class Direction(
    val rowValue: Int,
    val colValue: Int,
)

private fun Position.plus(rowValue: Int, colValue: Int): Position =
    copy(row = row + rowValue, col = col + colValue)

private fun Position.plusRow(rowValue: Int): Position =
    copy(row = row + rowValue)

private fun Position.plusCol(colValue: Int): Position =
    copy(col = col + colValue)

private data class StateChange(val newState: Cell)

private sealed class Cell
private object Floor : Cell()
private sealed class Seat : Cell()
private object EmptySeat : Seat()
private object OccupiedSeat : Seat()

private fun Char.toCell(): Cell = when (this) {
    '.' -> Floor
    'L' -> EmptySeat
    '#' -> OccupiedSeat
    else -> throw IllegalArgumentException("Unknown cell $this")
}
