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
    map { it.policyPasswordPair(policyFactoryMethod) }
        .count { (policy, password) ->
            policy.checkPassword(password)
        }

private typealias Password = String

private fun String.policyPasswordPair(
    policyFactoryMethod: PasswordPolicyFactoryMethod,
): Pair<PasswordPolicy, Password> {
    val (policyStr, passwordStr) = split(":")
    fun String.policy(): PasswordPolicy {
        val (rangeStr, charStr) = split(" ")
        val (leftNumber, rightNumber) = rangeStr.split("-")
        return policyFactoryMethod.create(
            charStr.first(),
            leftNumber.toInt(),
            rightNumber.toInt()
        )
    }
    return policyStr.policy() to passwordStr.trim()
}

private fun interface PasswordPolicyFactoryMethod {
    fun create(char: Char, leftNumber: Int, rightNumber: Int): PasswordPolicy
}

private object CharacterRangeEntryPasswordPolicyFactoryMethod : PasswordPolicyFactoryMethod {
    override fun create(char: Char, leftNumber: Int, rightNumber: Int): PasswordPolicy =
        CharacterEntryCountPasswordPolicy(
            char,
            leftNumber..rightNumber
        )
}

private object CharacterPositionEntryPasswordPolicyFactoryMethod : PasswordPolicyFactoryMethod {
    override fun create(char: Char, leftNumber: Int, rightNumber: Int): PasswordPolicy =
        CharacterEntryPositionPasswordPolicy(
            char,
            setOf(leftNumber - 1, rightNumber - 1)
        )
}

private interface PasswordPolicy {
    fun checkPassword(password: Password): Boolean
}

class CharacterEntryCountPasswordPolicy(
    private val char: Char,
    private val entryCountRange: IntRange,
) : PasswordPolicy {
    override fun checkPassword(password: Password): Boolean =
        password.count(char::equals) in entryCountRange
}

class CharacterEntryPositionPasswordPolicy(
    private val char: Char,
    private val entryPositions: Set<Int>,
) : PasswordPolicy {
    override fun checkPassword(password: Password): Boolean =
        entryPositions.asSequence().map(password::getOrNull).count(char::equals) == 1
}
