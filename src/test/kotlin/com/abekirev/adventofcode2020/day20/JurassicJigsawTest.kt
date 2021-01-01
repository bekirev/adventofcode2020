package com.abekirev.adventofcode2020.day20

import com.abekirev.adventofcode2020.day20.Direction.DOWN
import com.abekirev.adventofcode2020.day20.Direction.LEFT
import com.abekirev.adventofcode2020.day20.Direction.RIGHT
import com.abekirev.adventofcode2020.day20.Direction.UP
import com.abekirev.adventofcode2020.grid.Grid
import com.abekirev.adventofcode2020.grid.MutableMapGrid
import com.abekirev.adventofcode2020.grid.Position
import com.abekirev.adventofcode2020.grid.Size
import com.abekirev.adventofcode2020.grid.mirroredVerticalLine
import com.abekirev.adventofcode2020.grid.mirroredHorizontalLine
import com.abekirev.adventofcode2020.grid.rotatedHalfCircled
import com.abekirev.adventofcode2020.grid.rotatedRightAngleClockwise
import com.abekirev.adventofcode2020.grid.rotatedRightAngleCounterclockwise
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe

class JurassicJigsawTest : ShouldSpec({
    val positionMap = mapOf(
        Position(0, 0) to 1,
        Position(0, 1) to 2,
        Position(2, 1) to 3,
        Position(2, 0) to 4,
    )
    val grid: Grid<Int> = MutableMapGrid(Size(3, 2)) { row, col ->
        positionMap[Position(row, col)] ?: 5
    }
    should("Rotate grid half circle") {
        val transformedGrid = grid.rotatedHalfCircled()
        transformedGrid[0, 0] shouldBeExactly 3
        transformedGrid[0, 1] shouldBeExactly 4
        transformedGrid[2, 1] shouldBeExactly 1
        transformedGrid[2, 0] shouldBeExactly 2
    }
    should("Rotate right angle clockwise") {
        val transformedGrid = grid.rotatedRightAngleClockwise()
        transformedGrid[0, 0] shouldBeExactly 4
        transformedGrid[0, 2] shouldBeExactly 1
        transformedGrid[1, 2] shouldBeExactly 2
        transformedGrid[1, 0] shouldBeExactly 3
    }
    should("Rotate right angle counterclockwise") {
        val transformedGrid = grid.rotatedRightAngleCounterclockwise()
        transformedGrid[0, 0] shouldBeExactly 2
        transformedGrid[0, 2] shouldBeExactly 3
        transformedGrid[1, 2] shouldBeExactly 4
        transformedGrid[1, 0] shouldBeExactly 1
    }
    should("Mirrored vertical line grid") {
        val transformedGrid = grid.mirroredVerticalLine()
        transformedGrid[0, 0] shouldBeExactly 2
        transformedGrid[0, 1] shouldBeExactly 1
        transformedGrid[2, 1] shouldBeExactly 4
        transformedGrid[2, 0] shouldBeExactly 3
    }
    should("Mirrored horizontal line grid") {
        val transformedGrid = grid.mirroredHorizontalLine()
        transformedGrid[0, 0] shouldBeExactly 4
        transformedGrid[0, 1] shouldBeExactly 3
        transformedGrid[2, 1] shouldBeExactly 2
        transformedGrid[2, 0] shouldBeExactly 1
    }
    should("Return sides from grid") {
        grid.sideAt(UP) shouldBe listOf(1, 2)
        grid.sideAt(RIGHT) shouldBe listOf(2, 5, 3)
        grid.sideAt(DOWN) shouldBe listOf(4, 3)
        grid.sideAt(LEFT) shouldBe listOf(1, 5, 4)
    }
})