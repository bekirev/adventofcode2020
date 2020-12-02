package com.abekirev.adventofcode2020.day02

import com.abekirev.adventofcode2020.util.linesFromResource
import java.nio.file.Path
import kotlin.streams.asSequence

fun main() {
    partOne()
    partTwo()
}

private fun partOne() {
    println(
        result(CharacterRangeEntryPasswordPolicyFactory)
    )
}

private fun partTwo() {
    println(
        result(CharacterPositionEntryPasswordPolicyFactory)
    )
}

private fun result(policyFactory: PasswordPolicyFactory) =
    Path.of("input", "day02", "input.txt")
        .linesFromResource()
        .asSequence()
        .map { parsePolicyPasswordPair(it, policyFactory) }
        .count { (policy, password) ->
            policy.checkPassword(password)
        }

private typealias Password = String

private fun parsePolicyPasswordPair(str: String, policyFactory: PasswordPolicyFactory): Pair<PasswordPolicy, Password> {
    val (policyStr, passwordStr) = str.split(":")
    fun parsePolicy(str: String): PasswordPolicy {
        val (rangeStr, charStr) = str.split(" ")
        val (leftNumber, rightNumber) = rangeStr.split("-")
        return policyFactory.create(
            charStr.first(),
            leftNumber.toInt() - 1,
            rightNumber.toInt() - 1
        )
    }
    return parsePolicy(policyStr) to passwordStr.trim()
}

private interface PasswordPolicyFactory {
    fun create(char: Char, leftNumber: Int, rightNumber: Int): PasswordPolicy
}

private object CharacterRangeEntryPasswordPolicyFactory : PasswordPolicyFactory {
    override fun create(char: Char, leftNumber: Int, rightNumber: Int): PasswordPolicy =
        CharacterRangeEntryPasswordPolicy(
            char,
            leftNumber..rightNumber
        )
}

private object CharacterPositionEntryPasswordPolicyFactory : PasswordPolicyFactory {
    override fun create(char: Char, leftNumber: Int, rightNumber: Int): PasswordPolicy =
        CharacterPositionEntryPasswordPolicy(
            char,
            setOf(leftNumber, rightNumber)
        )
}

private interface PasswordPolicy {
    fun checkPassword(password: Password): Boolean
}

private class CharacterRangeEntryPasswordPolicy(
    private val char: Char,
    private val entryCountRange: IntRange,
) : PasswordPolicy {
    override fun checkPassword(password: Password): Boolean =
        password.count { it == char } in entryCountRange
}

private class CharacterPositionEntryPasswordPolicy(
    private val char: Char,
    private val entryPositions: Set<Int>,
) : PasswordPolicy {
    override fun checkPassword(password: Password): Boolean =
        entryPositions.asSequence().map(password::getOrNull).count(char::equals) == 1
}
