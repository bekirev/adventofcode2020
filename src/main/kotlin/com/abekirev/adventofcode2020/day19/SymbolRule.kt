package com.abekirev.adventofcode2020.day19

import com.abekirev.adventofcode2020.util.tail

class SymbolRule <S>(
    private val symbol: S,
) : Rule<S> {
    override fun consume(word: Word<S>): Sequence<Word<S>> =
        if (symbol == word.firstOrNull()) sequenceOf(word.tail)
        else emptySequence()
}