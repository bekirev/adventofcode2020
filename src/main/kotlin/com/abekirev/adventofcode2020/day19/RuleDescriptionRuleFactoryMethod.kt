package com.abekirev.adventofcode2020.day19

class RuleDescriptionRuleFactoryMethod<S>(
    private val rules: Map<RuleId, RuleDescription<S>>,
) : RuleFactoryMethod<S> {
    override fun createRule(ruleId: RuleId): Rule<S> =
        createRule(rules[ruleId] ?: throw NoSuchElementException("No rule with id $ruleId"))

    private fun createRule(ruleDescription: RuleDescription<S>): Rule<S> =
        when (ruleDescription) {
            is OrRuleDescription -> OrRule(ruleDescription.rules.map(this::createRule))
            is SuccessiveRuleDescription -> SuccessiveRule(ruleDescription.rules.map { { createRule(it) } })
            is ReferenceRuleDescription -> createRule(ruleDescription.ruleId)
            is SymbolRuleDescription -> SymbolRule(ruleDescription.symbol)
        }
}
