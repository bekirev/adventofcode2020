package com.abekirev.adventofcode2020.day02

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

private val INPUT_PATH = Path.of("input", "day02", "input.txt")

private fun partOne() {
    println(
        INPUT_PATH.useLinesFromResource { lines ->
            lines.validPasswordsCount(CharacterRangeEntryPasswordPolicyFactoryMethod)
        }
    )
}

private fun partTwo() {
    println(
        INPUT_PATH.useLinesFromResource { lines ->
            lines.validPasswordsCount(CharacterPositionEntryPasswordPolicyFactoryMethod)
        }
    )
}

private fun Sequence<String>.validPasswordsCount(policyFactoryMethod: PasswordPolicyFactoryMethod): Int =
    map { parsePolicyPasswordPair(it, policyFactoryMethod) }
        .count { (policy, password) ->
            policy.checkPassword(password)
        }

private typealias Password = String

private fun parsePolicyPasswordPair(
    str: String,
    policyFactoryMethod: PasswordPolicyFactoryMethod
): Pair<PasswordPolicy, Password> {
    val (policyStr, passwordStr) = str.split(":")
    fun parsePolicy(str: String): PasswordPolicy {
        val (rangeStr, charStr) = str.split(" ")
        val (leftNumber, rightNumber) = rangeStr.split("-")
        return policyFactoryMethod.create(
            charStr.first(),
            leftNumber.toInt() - 1,
            rightNumber.toInt() - 1
        )
    }
    return parsePolicy(policyStr) to passwordStr.trim()
}

private fun interface PasswordPolicyFactoryMethod {
    fun create(char: Char, leftNumber: Int, rightNumber: Int): PasswordPolicy
}

private object CharacterRangeEntryPasswordPolicyFactoryMethod : PasswordPolicyFactoryMethod {
    override fun create(char: Char, leftNumber: Int, rightNumber: Int): PasswordPolicy =
        CharacterRangeEntryPasswordPolicy(
            char,
            leftNumber..rightNumber
        )
}

private object CharacterPositionEntryPasswordPolicyFactoryMethod : PasswordPolicyFactoryMethod {
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
