package com.abekirev.adventofcode2020.day20

import com.abekirev.adventofcode2020.day20.Direction.DOWN
import com.abekirev.adventofcode2020.day20.Direction.LEFT
import com.abekirev.adventofcode2020.day20.Direction.RIGHT
import com.abekirev.adventofcode2020.day20.Direction.UP

enum class Direction {
    UP,
    DOWN,
    RIGHT,
    LEFT,
    ;
}

val Direction.opposite
    get() = when (this) {
        UP -> DOWN
        DOWN -> UP
        RIGHT -> LEFT
        LEFT -> RIGHT
    }
