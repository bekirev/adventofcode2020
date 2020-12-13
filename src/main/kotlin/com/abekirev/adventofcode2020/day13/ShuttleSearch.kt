package com.abekirev.adventofcode2020.day13

import com.abekirev.adventofcode2020.util.lcm
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.math.BigInteger
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

private fun partOne() =
    println(
        Path.of("input", "day13", "input.txt").useLinesFromResource { lines ->
            val input = lines.toList()
            val startTime = input[0].toInt()
            val busPeriods = input[1].split(',').mapNotNull { it.toIntOrNull() }
            val (departTime, period) = firstNumberDividerPair(startTime, busPeriods)
            (departTime - startTime) * period
        }
    )

fun firstNumberDividerPair(startNumber: Int, dividers: Collection<Int>): NumberDividerPair {
    if (startNumber <= 0)
        throw IllegalArgumentException("Start number should be positive")
    if (dividers.isEmpty())
        throw IllegalArgumentException("dividers collection shouldn't be empty")
    return generateSequence(startNumber, 1::plus)
        .flatMap { numberAfter ->
            dividers.asSequence()
                .mapNotNull { divider ->
                    if (numberAfter % divider == 0) NumberDividerPair(numberAfter, divider)
                    else null
                }
        }
        .first()
}

data class NumberDividerPair(
    val number: Int,
    val divider: Int,
)

private fun partTwo() =
    println(
        Path.of("input", "day13", "input.txt").useLinesFromResource { lines ->
            val schedule =
                lines.toList()[1].split(',').map { it.toLongOrNull() }.dropWhile { it == null }.toMutableList()
            val startDividerNumberRule = CurrentDividerNumberRule(schedule.first()!!.toBigInteger())
            schedule[0] = null
            val offsetDividerNumberRules = schedule.offsetsFromGivenIndexFromOtherElements(0).map { (period, offset) ->
                OffsetDividerNumberRule(period.toBigInteger(), offset.toBigInteger())
            }.toList()
            numberThatCompliesWithAllNumberRules(
                startDividerNumberRule,
                offsetDividerNumberRules
            )
        }
    )

fun numberThatCompliesWithAllNumberRules(
    startDividerNumberRule: CurrentDividerNumberRule,
    offsetRules: Collection<OffsetDividerNumberRule>,
): BigInteger {
    tailrec fun nextNumberCompliesWithRule(
        start: BigInteger,
        delta: BigInteger,
        rule: OffsetDividerNumberRule,
    ): BigInteger = when {
        start compliesWith rule -> start
        else -> nextNumberCompliesWithRule(start + delta, delta, rule)
    }

    var curNumber: BigInteger = startDividerNumberRule.divider
    var lcm: BigInteger = startDividerNumberRule.divider
    for (rule in offsetRules) {
        curNumber = nextNumberCompliesWithRule(curNumber, lcm, rule)
        lcm = lcm(lcm, rule.divider)
    }
    return curNumber
}

interface DividerNumberRule {
    val divider: BigInteger
    fun check(number: BigInteger): Boolean
}

infix fun BigInteger.compliesWith(rule: DividerNumberRule): Boolean =
    rule.check(this)

class CurrentDividerNumberRule(
    override val divider: BigInteger,
) : DividerNumberRule {
    init {
        check(divider != BigInteger.ZERO) { "Divider shouldn't be 0" }
    }

    override fun check(number: BigInteger): Boolean =
        number % divider == BigInteger.ZERO

}

class OffsetDividerNumberRule private constructor(
    private val currentDividerNumberRule: CurrentDividerNumberRule,
    private val offset: BigInteger,
) : DividerNumberRule {
    constructor(
        divider: BigInteger,
        offset: BigInteger,
    ) : this(
        CurrentDividerNumberRule(divider),
        offset
    )

    override val divider: BigInteger
        get() = currentDividerNumberRule.divider

    override fun check(number: BigInteger): Boolean =
        (number + offset) compliesWith currentDividerNumberRule
}

fun <T : Any> List<T?>.offsetsFromGivenIndexFromOtherElements(index: Int): Sequence<Pair<T, Int>> = sequence {
    for ((itIndex, elem) in this@offsetsFromGivenIndexFromOtherElements.withIndex()) {
        if (elem != null) {
            yield(elem to itIndex - index)
        }
    }
}

