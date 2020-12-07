package com.abekirev.adventofcode2020.day07

import com.abekirev.adventofcode2020.util.groupByTo
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
}

private fun partOne() =
    println(
        Path.of("input", "day07", "input.txt")
            .useLinesFromResource { lines ->
                bagColorsCanContainCertainColor(
                    containedInMap = lines
                        .rules()
                        .flatMap<Rule, Pair<BagColor, BagColor>> { (bagColor, restrictions) ->
                            restrictions.asSequence<Restriction>()
                                .map<Restriction, BagColor>(Restriction::bagColor)
                                .map<BagColor, Pair<BagColor, BagColor>> { it to bagColor }
                        }
                        .groupByTo(
                            mutableMapOf(),
                            ::mutableSetOf,
                            Pair<BagColor, BagColor>::first,
                            Pair<BagColor, BagColor>::second,
                        ),
                    "shiny gold"
                ).size
            }
    )

private fun bagColorsCanContainCertainColor(
    containedInMap: Map<BagColor, Set<BagColor>>,
    bagColor: BagColor,
): Set<BagColor> =
    when (val containedInColors = containedInMap[bagColor]) {
        is Set -> containedInColors.plus(
            containedInColors.asSequence().flatMap {
                bagColorsCanContainCertainColor(containedInMap, it)
            }
        )
        else -> emptySet()
    }

private fun Sequence<String>.rules(): Sequence<Rule> = map(String::parseRule)


private fun String.parseRule(): Rule {
    val (bagColorStr, restrictionsStr) = dropLast(1).split(" contain ")
    return Rule(
        bagColorStr.parseBagColor(),
        when (restrictionsStr) {
            "no other bags" -> emptySet()
            else -> restrictionsStr
                .split(", ")
                .mapTo(mutableSetOf(), String::parseRestriction)
        }

    )
}

private fun String.parseBagColor(): BagColor {
    return substringBeforeLast(" bag")
}

private fun String.parseRestriction(): Restriction {
    val (quantity, bagColorStr) = split(' ', limit = 2)
    return Restriction(
        bagColorStr.parseBagColor(),
        quantity.toInt()
    )
}

private data class Rule(
    val bagColor: BagColor,
    val restrictions: Set<Restriction>,
)

private typealias BagColor = String

private data class Restriction(
    val bagColor: BagColor,
    val quantity: Int,
)