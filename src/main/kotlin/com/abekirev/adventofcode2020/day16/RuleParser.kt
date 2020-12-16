package com.abekirev.adventofcode2020.day16

interface RuleParser {
    fun parse(string: String): Rule
}

interface Rule {
    val field: String
    val firstRange: IntRange
    val secondRange: IntRange
}
