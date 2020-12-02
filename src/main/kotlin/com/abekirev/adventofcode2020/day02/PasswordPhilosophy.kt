package com.abekirev.adventofcode2020.day02

import com.abekirev.adventofcode2020.util.linesFromResource
import java.nio.file.Path
import kotlin.streams.asSequence

fun main() {
    partOne()
}

private fun partOne() {
    println(
        Path.of("input", "day02", "input.txt")
            .linesFromResource()
            .asSequence()
            .map(::parsePolicyPasswordPair)
            .count { (policy, password) ->
                policy.checkPassword(password)
            }
    )
}

private typealias Password = String

private fun parsePolicyPasswordPair(str: String): Pair<PasswordPolicy, Password> {
    val (policyStr, passwordStr) = str.split(":")
    fun parsePolicy(str: String): PasswordPolicy {
        val (rangeStr, charStr) = str.split(" ")
        val (leftBound, rightBound) = rangeStr.split("-")
        return CharacterEntryPasswordPolicy(
            charStr.first(),
            leftBound.toInt()..rightBound.toInt()
        )
    }
    return parsePolicy(policyStr) to passwordStr.trim()
}

private interface PasswordPolicy {
    fun checkPassword(password: Password): Boolean
}

private class CharacterEntryPasswordPolicy(
    private val char: Char,
    private val entryCountRange: IntRange
) : PasswordPolicy {
    override fun checkPassword(password: Password): Boolean =
        password.count { it == char } in entryCountRange
}
