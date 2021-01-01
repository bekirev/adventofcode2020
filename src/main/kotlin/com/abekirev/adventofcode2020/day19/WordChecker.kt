package com.abekirev.adventofcode2020.day19

typealias Word<S> = List<S>

interface WordChecker<S> {
    fun check(word: Word<S>): Boolean
}
