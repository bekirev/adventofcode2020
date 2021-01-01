package com.abekirev.adventofcode2020.day19

sealed class RuleDescription<S>
class OrRuleDescription<S>(val rules: List<RuleDescription<S>>) : RuleDescription<S>()
class SuccessiveRuleDescription<S>(val rules: List<RuleDescription<S>>) : RuleDescription<S>()
class ReferenceRuleDescription<S>(val ruleId: RuleId) : RuleDescription<S>()
class SymbolRuleDescription<S>(val symbol: S) : RuleDescription<S>()

fun parseRuleDescription(str: String): Pair<RuleId, RuleDescription<Token<Char>>> {
    val (ruleIdStr, ruleStr) = str.split(": ")
    val ruleId = ruleIdStr.toInt()
    return ruleId to when {
        ruleStr.contains(" | ") -> {
            val (leftStr, rightStr) = ruleStr.split(" | ")
            OrRuleDescription(
                listOf(
                    successiveRuleDescription(leftStr),
                    successiveRuleDescription(rightStr),
                )
            )
        }
        ruleStr.contains("\"") -> SymbolRuleDescription(SymbolToken(ruleStr[1]))
        else -> successiveRuleDescription(ruleStr)
    }
}

private fun successiveRuleDescription(str: String): RuleDescription<Token<Char>> {
    val elements = str.split(" ")
    return if (elements.size == 1)
        ReferenceRuleDescription(elements.first().toInt())
    else
        SuccessiveRuleDescription(
            elements
                .asSequence()
                .filter(String::isNotEmpty)
                .map(String::toInt)
                .map { ReferenceRuleDescription<Token<Char>>(it) }
                .toList()
        )
}