package com.abekirev.adventofcode2020.day23

fun main() {
    partOne()
}

private fun partOne() =
    println(
        "389547612".map { it.toString().toInt() }
            .labelsAfterOne(100)
            .joinToString("")
    )

typealias Cup = Int

fun List<Cup>.labelsAfterOne(movesCount: Int): List<Cup> =
    after(movesCount).reorderCircledWithStartElement(1).drop(1)

fun <T> List<T>.reorderCircledWithStartElement(elem: T): List<T> = when (val elemIndex = indexOf(elem)) {
    -1 -> throw IllegalArgumentException("Element $elem is not in list $this")
    else -> reorderCircledWithStartIndex(elemIndex)
}

fun <T> List<T>.reorderCircledWithStartIndex(index: Int): List<T> = when (index) {
    0 -> this
    else -> subList(index, size) + subList(0, index)
}

fun List<Cup>.after(movesCount: Int): List<Cup> {
    var cups = this
    repeat(movesCount) {
        cups = move(cups)
    }
    return cups
}

fun move(cups: List<Cup>): List<Cup> {
    val pickup = cups.subList(1, 4)
    val first = cups.first()
    return when (val destinationCup = cups.destinationCup(pickup)) {
        first -> cups
        else -> sequenceOf(
            sequenceOf(first),
            cups.subList(4, cups.size).asSequence()
        ).flatten().placeAfter(destinationCup, pickup).toList()
    }.reorderCircledWithStartIndex(1)
}

fun <T> Sequence<T>.placeAfter(elem: T, list: List<T>): Sequence<T> = sequence {
    for (it in this@placeAfter) {
        when (it) {
            elem -> {
                yield(elem)
                yieldAll(list)
            }
            else -> yield(it)
        }
    }
}

fun List<Cup>.destinationCup(pickup: List<Cup>): Cup {
    val min = minOrNull()!!
    val max = maxOrNull()!!
    tailrec fun destinationCup(destinationCup: Cup): Cup =
        when {
            destinationCup < min -> destinationCup(max)
            destinationCup in pickup -> destinationCup(destinationCup - 1)
            else -> destinationCup
        }
    return destinationCup(first() - 1)
}
