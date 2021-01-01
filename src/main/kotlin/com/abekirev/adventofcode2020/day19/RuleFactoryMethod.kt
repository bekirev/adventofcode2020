package com.abekirev.adventofcode2020.day19

typealias RuleId = Int

interface RuleFactoryMethod<S> {
    fun createRule(ruleId: RuleId): Rule<S>
}