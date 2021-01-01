package com.abekirev.adventofcode2020.day19

class RuleWordChecker<S>(
    private val ruleFactoryMethod: RuleFactoryMethod<S>,
    private val rootRuleId: RuleId,
) : WordChecker<S> {
    override fun check(word: Word<S>): Boolean =
        ruleFactoryMethod
            .createRule(rootRuleId)
            .consume(word)
            .any(Word<S>::isEmpty)
}