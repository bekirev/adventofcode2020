package com.abekirev.adventofcode2020.day19

import com.abekirev.adventofcode2020.util.odd
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
}

private fun partOne() {
    val messageCheckService = Path.of("input", "day19", "rules.txt").useLinesFromResource { lines ->
        RuleMessageCheckService(
            lines
                .map(String::parseRule)
        )
    }
    println(
        Path.of("input", "day19", "messages.txt").useLinesFromResource { lines ->
            lines
                .count { msg -> messageCheckService.check(0, msg) }
        }
    )
}

private val SUCCESSIVE_RULE_REGEX = Regex("""(\d+)(\s(\d+))*""")
private val SINGLE_CHAR_RULE_REGEX = Regex(""""(\w)"""")
private fun String.successiveRule(): SuccessiveRule =
    SuccessiveRule(
        splitToSequence(" ")
            .filter(String::isNotEmpty)
            .map(String::toInt)
            .map(::RuleRefRule)
    )

private fun String.parseRule(): Pair<RuleId, Rule> {
    val (ruleIdStr, ruleStr) = split(": ")
    val ruleId = ruleIdStr.toInt()
    val rulePairFunc = { rule: Rule -> ruleId to rule }
    val successiveMatches = SUCCESSIVE_RULE_REGEX.matchEntire(ruleStr)
    if (successiveMatches != null) {
        return rulePairFunc(
            ruleStr.successiveRule()
        )
    }
    if (ruleStr.contains(" | ")) {
        val (leftStr, rightStr) = ruleStr.split(" | ")
        return rulePairFunc(
            OrRule(
                leftStr.successiveRule(),
                rightStr.successiveRule(),
            )
        )
    }
    val singleCharMatches = SINGLE_CHAR_RULE_REGEX.matchEntire(ruleStr)
    if (singleCharMatches != null) {
        return rulePairFunc(
            SingleCharRule(singleCharMatches.groupValues[1].first())
        )
    }
    throw IllegalArgumentException("Unknown rule $this")
}

private interface MessageCheckService {
    fun check(ruleId: RuleId, msg: Message): Boolean
}

private class RuleMessageCheckService(
    private val context: RuleContext,
) : MessageCheckService {
    constructor(rules: Sequence<Pair<RuleId, Rule>>) : this(MapRuleContext(rules.toMap()))

    override fun check(ruleId: RuleId, msg: Message): Boolean =
        when (val checkResult = context[ruleId].check(context, msg)) {
            InvalidCheckResult -> false
            is ValidCheckResult -> when (checkResult.checkedCharCount) {
                msg.length -> true
                else -> false
            }
        }
}

private interface Rule {
    fun check(context: RuleContext, msg: Message): CheckResult
}

private class OrRule(
    private val leftRule: Rule,
    private val rightRule: Rule,
) : Rule {
    override fun check(context: RuleContext, msg: Message): CheckResult {
        return when (val leftCheckResult = leftRule.check(context, msg)) {
            is ValidCheckResult -> leftCheckResult
            else -> rightRule.check(context, msg)
        }
    }
}

private class SuccessiveRule(
    private val rules: List<Rule>,
) : Rule {
    constructor(rules: Sequence<Rule>) : this(rules.toList())

    override fun check(context: RuleContext, msg: Message): CheckResult {
        var checkedCharCount = 0
        var msgToCheck = msg
        for (rule in rules) {
            when (val checkResult = rule.check(context, msgToCheck)) {
                is ValidCheckResult -> {
                    checkedCharCount += checkResult.checkedCharCount
                    msgToCheck = msgToCheck.subSequence(checkResult.checkedCharCount..msgToCheck.lastIndex)
                }
                else -> return InvalidCheckResult
            }
        }
        return ValidCheckResult(checkedCharCount)
    }
}

private class RuleRefRule(
    private val ruleId: RuleId,
) : Rule {
    override fun check(context: RuleContext, msg: Message): CheckResult =
        context[ruleId].check(context, msg)
}

private class SingleCharRule(
    private val char: Char,
) : Rule {
    override fun check(context: RuleContext, msg: Message): CheckResult =
        when (msg.first() == char) {
            true -> ValidCheckResult(1)
            false -> InvalidCheckResult
        }
}

private interface RuleContext {
    operator fun get(ruleId: RuleId): Rule
}

private class MapRuleContext(
    private val rulesById: Map<RuleId, Rule>,
) : RuleContext {
    override fun get(ruleId: RuleId): Rule =
        rulesById[ruleId] ?: throw IllegalArgumentException("No rule with id $ruleId")
}

private typealias RuleId = Int
private typealias Message = CharSequence

private sealed class CheckResult
private object InvalidCheckResult : CheckResult()
private data class ValidCheckResult(val checkedCharCount: Int) : CheckResult()