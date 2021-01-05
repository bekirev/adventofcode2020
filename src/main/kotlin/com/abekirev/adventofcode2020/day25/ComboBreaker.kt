package com.abekirev.adventofcode2020.day25

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
}

private fun partOne() {
    val startValue = 1
    val divider = 20201227
    val firstPhaseSubjectValue = 7
    val (firstHash, secondHash) = Path.of("input", "day25", "input.txt").useLinesFromResource { lines ->
        lines.take(2).map(String::toInt).toList()
    }
    val encryptionKey = findEncryptionKey(
        startValue,
        firstPhaseSubjectValue,
        divider,
        firstHash,
        secondHash
    )
    println(encryptionKey)
}

fun findEncryptionKey(
    startValue: Int,
    firstPhaseSubjectValue: Int,
    divider: Int,
    firstPublicKey: Int,
    secondPublicKey: Int,
): Int {
    val firstPhaseHasher = ComboBreakerHasher(firstPhaseSubjectValue, divider)
    val firstLoopSize = findLoopSizeForValue(firstPhaseHasher, firstPublicKey, startValue)
    val secondPhaseHasher = ComboBreakerHasher(secondPublicKey, divider)
    return secondPhaseHasher.hash(startValue, firstLoopSize)
}

fun findLoopSizeForValue(publicKey: Hasher, value: Int, startValue: Int): Int {
    tailrec fun findLoopSizeForValue(startValue: Int, loopSize: Int): Int = when (startValue) {
        value -> loopSize
        else -> findLoopSizeForValue(publicKey.hash(startValue), loopSize + 1)
    }
    return findLoopSizeForValue(startValue, 0)
}

tailrec fun Hasher.hash(value: Int, loopSize: Int = 1): Int = when (loopSize) {
    0 -> value
    else -> hash(hash(value), loopSize - 1)
}

interface Hasher {
    fun hash(value: Int): Int
}

class ComboBreakerHasher private constructor(
    private val subjectNumber: Long,
    private val divider: Long,
) : Hasher {
    constructor(
        subjectNumber: Int,
        divider: Int,
    ) : this(
        subjectNumber.toLong(),
        divider.toLong()
    )

    override fun hash(value: Int): Int =
        (value.toLong() * subjectNumber % divider).toInt()
}
