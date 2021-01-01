package com.abekirev.adventofcode2020.day19

import com.abekirev.adventofcode2020.util.append

sealed class Token<out T>
data class SymbolToken<T>(val symbol: T) : Token<T>()
object EndToken : Token<Nothing>()

fun String.tokenized(): List<Token<Char>> =
    asSequence()
        .map<Char, Token<Char>>(::SymbolToken)
        .append(EndToken)
        .toList()
