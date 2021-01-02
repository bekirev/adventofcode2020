package com.abekirev.adventofcode2020.grid

class MutableMapGrid<T : Any> private constructor(
    override val size: Size,
    private val map: MutableMap<Position, T>,
) : MutableGrid<T> {
    constructor(
        size: Size,
        initFun: (row: Int, col: Int) -> T,
    ) : this(
        size,
        { (row, col) -> initFun(row, col) },
    )

    constructor(
        size: Size,
        initFun: (position: Position) -> T,
    ) : this(
        size,
        mutableMapOf<Position, T>().apply {
            size
                .positions()
                .forEach { pos -> this[pos] = initFun(pos) }
        },
    )

    override fun get(row: Int, col: Int): T =
        map[Position(row, col)]
            ?: throw IndexOutOfBoundsException("Position with row $row and col $col is out of grid with size $size")

    override fun get(pos: Position): T =
        map[pos]
            ?: throw IndexOutOfBoundsException("Position with row ${pos.row} and col ${pos.col} is out of grid with size $size")

    override fun set(updatedCells: Map<Position, T>) = this.apply {
        updatedCells.forEach(map::put)
    }
}
