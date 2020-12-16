package com.abekirev.adventofcode2020.day16

object RuleParserImpl : RuleParser {
    override fun parse(string: String): Rule {
        val (fieldStr, rangesStr) = string.split(": ")
        val (leftRangeStr, rightRangeStr) = rangesStr.split(" or ")
        return RuleImpl(
            fieldStr,
            parseRange(leftRangeStr),
            parseRange(rightRangeStr),
        )
    }

    private fun parseRange(string: String): IntRange {
        val (leftNumber, rightNumber) = string.split("-")
        return IntRange(leftNumber.toInt(), rightNumber.toInt())
    }
}
