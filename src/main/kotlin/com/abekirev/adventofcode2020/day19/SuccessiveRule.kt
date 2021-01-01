package com.abekirev.adventofcode2020.day19

class SuccessiveRule<S>(
    private val rules: List<() -> Rule<S>>,
) : Rule<S> {
    override fun consume(word: Word<S>): Sequence<Word<S>> {
        var validWordBeginnings = listOf(word)
        for (ruleCreator in rules) {
            if (validWordBeginnings.isEmpty()) return emptySequence()
            validWordBeginnings = validWordBeginnings.flatMap {
                ruleCreator().consume(it)
            }
        }
        return validWordBeginnings.asSequence()
    }
}