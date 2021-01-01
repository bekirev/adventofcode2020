package com.abekirev.adventofcode2020.day19

class OrRule<S>(
    private var rules: List<Rule<S>>,
) : Rule<S> {
    override fun consume(word: Word<S>): Sequence<Word<S>> =
        rules.asSequence()
            .flatMap { it.consume(word) }
}