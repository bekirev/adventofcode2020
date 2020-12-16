package com.abekirev.adventofcode2020.day16

data class RuleImpl(
    override val field: String,
    override val firstRange: IntRange,
    override val secondRange: IntRange,
) : Rule
