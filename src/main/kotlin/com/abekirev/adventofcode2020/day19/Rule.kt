package com.abekirev.adventofcode2020.day19

interface Rule<S> {
    fun consume(word: Word<S>): Sequence<Word<S>>
}
