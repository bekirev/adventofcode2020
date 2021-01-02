package com.abekirev.adventofcode2020.day20

import com.abekirev.adventofcode2020.day20.Direction.DOWN
import com.abekirev.adventofcode2020.day20.Direction.LEFT
import com.abekirev.adventofcode2020.day20.Direction.RIGHT
import com.abekirev.adventofcode2020.day20.Direction.UP
import com.abekirev.adventofcode2020.grid.Grid
import com.abekirev.adventofcode2020.grid.MergeVerticalSideGrid
import com.abekirev.adventofcode2020.grid.MergedHorizontalSideGrid
import com.abekirev.adventofcode2020.grid.MutableMapGrid
import com.abekirev.adventofcode2020.grid.Position
import com.abekirev.adventofcode2020.grid.Size
import com.abekirev.adventofcode2020.grid.mirroredHorizontalLine
import com.abekirev.adventofcode2020.grid.mirroredVerticalLine
import com.abekirev.adventofcode2020.grid.positionWithValues
import com.abekirev.adventofcode2020.grid.rotatedHalfCircled
import com.abekirev.adventofcode2020.grid.rotatedRightAngleClockwise
import com.abekirev.adventofcode2020.grid.rotatedRightAngleCounterclockwise
import com.abekirev.adventofcode2020.grid.withoutBorders
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe

class JurassicJigsawTest : ShouldSpec({
    val charToInt: Char.() -> Int = { toString().toInt() }
    val positionMap = """
        12
        55
        43
    """.trimIndent()
        .splitToSequence("\n")
        .toGrid(charToInt)
    val grid: Grid<Int> = MutableMapGrid(Size(3, 2)) { row, col ->
        positionMap[Position(row, col)]
    }
    should("Rotate grid half circle") {
        val transformedGrid = grid.rotatedHalfCircled()
        transformedGrid.size shouldBe Size(3, 2)
        transformedGrid[0, 0] shouldBeExactly 3
        transformedGrid[0, 1] shouldBeExactly 4
        transformedGrid[2, 1] shouldBeExactly 1
        transformedGrid[2, 0] shouldBeExactly 2
    }
    should("Rotate right angle clockwise") {
        val transformedGrid = grid.rotatedRightAngleClockwise()
        transformedGrid.size shouldBe Size(2, 3)
        transformedGrid[0, 0] shouldBeExactly 4
        transformedGrid[0, 2] shouldBeExactly 1
        transformedGrid[1, 2] shouldBeExactly 2
        transformedGrid[1, 0] shouldBeExactly 3
    }
    should("Rotate right angle counterclockwise") {
        val transformedGrid = grid.rotatedRightAngleCounterclockwise()
        transformedGrid.size shouldBe Size(2, 3)
        transformedGrid[0, 0] shouldBeExactly 2
        transformedGrid[0, 2] shouldBeExactly 3
        transformedGrid[1, 2] shouldBeExactly 4
        transformedGrid[1, 0] shouldBeExactly 1
    }
    should("Mirrored vertical line grid") {
        val transformedGrid = grid.mirroredVerticalLine()
        transformedGrid.size shouldBe Size(3, 2)
        transformedGrid[0, 0] shouldBeExactly 2
        transformedGrid[0, 1] shouldBeExactly 1
        transformedGrid[2, 1] shouldBeExactly 4
        transformedGrid[2, 0] shouldBeExactly 3
    }
    should("Mirrored horizontal line grid") {
        val transformedGrid = grid.mirroredHorizontalLine()
        transformedGrid.size shouldBe Size(3, 2)
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
    should("Merge two grid vertical line") {
        val mergedGrid = MergeVerticalSideGrid(
            sequenceOf(
                "12",
                "34",
            ).toGrid(charToInt),
            sequenceOf(
                "567",
                "890",
            ).toGrid(charToInt)
        )
        mergedGrid.size shouldBe Size(2, 5)
        mergedGrid[0, 0] shouldBeExactly 1
        mergedGrid[0, 1] shouldBeExactly 2
        mergedGrid[0, 2] shouldBeExactly 5
        mergedGrid[0, 3] shouldBeExactly 6
        mergedGrid[0, 4] shouldBeExactly 7
        mergedGrid[1, 0] shouldBeExactly 3
        mergedGrid[1, 1] shouldBeExactly 4
        mergedGrid[1, 2] shouldBeExactly 8
        mergedGrid[1, 3] shouldBeExactly 9
        mergedGrid[1, 4] shouldBeExactly 0
    }
    should("Merge two grid horizontal line") {
        val mergedGrid = MergedHorizontalSideGrid(
            sequenceOf(
                "12",
                "34",
            ).toGrid(charToInt),
            sequenceOf(
                "56",
                "78",
                "90",
            ).toGrid(charToInt)
        )
        mergedGrid.size shouldBe Size(5, 2)
        mergedGrid[0, 0] shouldBeExactly 1
        mergedGrid[0, 1] shouldBeExactly 2
        mergedGrid[1, 0] shouldBeExactly 3
        mergedGrid[1, 1] shouldBeExactly 4
        mergedGrid[2, 0] shouldBeExactly 5
        mergedGrid[2, 1] shouldBeExactly 6
        mergedGrid[3, 0] shouldBeExactly 7
        mergedGrid[3, 1] shouldBeExactly 8
        mergedGrid[4, 0] shouldBeExactly 9
        mergedGrid[4, 1] shouldBeExactly 0
    }
    should("Remove border from grid") {
        val transformedGrid = sequenceOf(
            "1234",
            "1692",
            "6547",
        ).toGrid(charToInt).withoutBorders(1)
        transformedGrid.size shouldBe Size(1, 2)
        transformedGrid[0, 0] shouldBeExactly 6
        transformedGrid[0, 1] shouldBeExactly 9
    }
    should("Search monster pattern") {
        val grid = sequenceOf(
            ".#..",
            "#.#.",
            ".#.#",
            "..#.",
        ).toGrid(Char::toSurface)
        MonsterScanner(
            sequenceOf(
                " # ",
                "# #",
                " # ",
            ).toGrid(Char::toGridCellPattern)
        ).scan(grid).distinct().toList() shouldBe listOf(
            Position(0, 1),
            Position(1, 0),
            Position(1, 2),
            Position(2, 1),
            Position(2, 3),
            Position(3, 2),
        )
    }
    should("Parse sea monster pattern") {
        val grid = """
            .#.#..#.##...#.##..#####
            ###....#.#....#..#......
            ##.##.###.#.#..######...
            ###.#####...#.#####.#..#
            ##.#....#.##.####...#.##
            ...########.#....#####.#
            ....#..#...##..#.#.###..
            .####...#..#.....#......
            #..#.##..#..###.#.##....
            #.####..#.####.#.#.###..
            ###.#.#...#.######.#..##
            #.####....##..########.#
            ##..##.#...#...#.#.#.#..
            ...#..#..#.#.##..###.###
            .#.#....#.##.#...###.##.
            ###.#...#..#.##.######..
            .#.#.###.##.##.#..#.##..
            .####.###.#...###.#..#.#
            ..#.#..#..#.#.#.####.###
            #..####...#.#.#.###.###.
            #####..#####...###....##
            #.##..#..#...#..####...#
            .#.###..##..##..####.##.
            ...###...##...#...#..###
        """.trimIndent().splitToSequence("\n").toGrid(Char::toSurface)
        val monsterPattern = seaMonsterPatternGrid()
        val scanners: Sequence<MonsterScanner> = sequenceOf(
            sequenceOf(monsterPattern),
            possibleTransformations<CellPattern<Surface>>().map { it(monsterPattern) }
        ).flatten()
            .map(::MonsterScanner)
        val firstTypeCount = grid.positionWithValues().map { it.second }.count(Surface.TYPE_1::equals)
        val monsterCellCount = scanners.flatMap { it.scan(grid) }.distinct().count()
        firstTypeCount - monsterCellCount shouldBeExactly 273
    }
})