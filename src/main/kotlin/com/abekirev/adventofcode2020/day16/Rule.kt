package com.abekirev.adventofcode2020.day16

interface Rule {
    val field: String
    val firstRange: IntRange
    val secondRange: IntRange
}
