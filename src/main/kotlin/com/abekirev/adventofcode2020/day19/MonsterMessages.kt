package com.abekirev.adventofcode2020.day19

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

private fun partOne() =
    println(
        countValidMessages()
    )

private fun partTwo() =
    println(
        countValidMessages(
            sequenceOf(
                "8: 42 | 42 8",
                "11: 42 31 | 42 11 31",
            )
        )
    )

private fun countValidMessages(
    additionalRules: Sequence<String> = emptySequence(),
): Int {
    val ruleWordChecker = Path.of("input", "day19", "rules.txt").useLinesFromResource { lines ->
        wordChecker(
            sequenceOf(
                lines,
                additionalRules
            ).flatten()
        )
    }
    return Path.of("input", "day19", "messages.txt").useLinesFromResource { lines ->
        lines
            .map { it.tokenized() }
            .count { msg -> ruleWordChecker.check(msg) }
    }
}

fun wordChecker(rules: Sequence<String>): WordChecker<Token<Char>> =
    RuleWordChecker(
        RuleDescriptionRuleFactoryMethod(
            sequenceOf(
                sequenceOf<Pair<RuleId, RuleDescription<Token<Char>>>>(
                    -1 to SuccessiveRuleDescription(
                        listOf(
                            ReferenceRuleDescription(0),
                            SymbolRuleDescription(EndToken)
                        )
                    )
                ),
                rules.map(::parseRuleDescription),
            ).flatten()
                .toMap()
        ),
        -1
    )
