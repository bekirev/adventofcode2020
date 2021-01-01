package com.abekirev.adventofcode2020.day20

class TileSideRegistry private constructor(
    private val tilesBySide: Map<Pair<TileSide, Direction>, Collection<Tile>>,
) {
    constructor(tiles: Sequence<Tile>) : this(
        tiles
            .flatMap { tile ->
                Direction.values().asSequence()
                    .map { direction ->
                        (tile.sideAt(direction) to direction) to tile
                    }
            }
            .groupBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            )
    )

    fun tilesWithSide(side: TileSide, direction: Direction): Collection<Tile> =
        tilesBySide[side to direction] ?: emptyList()
}

fun TileSideRegistry.tilesSequence(startTile: Tile, direction: Direction): Sequence<Tile> = sequence {
    var tile: Tile = startTile
    while (true) {
        yield(tile)
        val nextTile = this@tilesSequence.tilesWithSide(tile.sideAt(direction), direction.opposite)
            .firstOrNull { tile.id != it.id }
        if (nextTile != null) tile = nextTile
        else break
    }
}
