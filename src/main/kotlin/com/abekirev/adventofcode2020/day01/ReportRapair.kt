package com.abekirev.adventofcode2020.day01

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

private val INPUT_PATH = Path.of("input", "day01", "input.txt")

private fun partOne() {
    printResult(2)
}

private fun partTwo() {
    printResult(3)
}

private fun printResult(numberOfEntries: Int) {
    println(
        when (val result = productOfEntriesWithSpecifiedSum(numberOfEntries, 2020)) {
            is Long -> result
            else -> "No such entries"
        }
    )
}

private fun productOfEntriesWithSpecifiedSum(numberOfEntries: Int, sum: Long) =
    INPUT_PATH.useLinesFromResource { lines ->
        lines
            .map(String::toLong)
            .findTupleWithSum(sum, numberOfEntries)
            .let { tuple -> tuple?.asList()?.product() }
    }

private fun Iterable<Long>.product(): Long? {
    val it = iterator()
    var result: Long? = null
    if (it.hasNext()) {
        result = it.next()
        while (it.hasNext()) {
            result *= it.next()
        }
    }
    return result
}

private fun Sequence<Long>.findTupleWithSum(sum: Long, size: Int): Tuple<Long>? =
    tuples(size)
        .firstOrNull { tuple -> tuple.asList().sum() == sum }

private fun <T> Sequence<T>.tuples(size: Int): Sequence<Tuple<T>> {
    fun <T> Sequence<T>.pairs(): Sequence<Tuple<T>> = sequence {
        val it = this@pairs.iterator()
        if (it.hasNext()) {
            val prevElems = mutableSetOf<T>()
            prevElems += it.next()
            while (it.hasNext()) {
                val elem = it.next()
                yieldAll(
                    prevElems.map { prevElem -> ListTuple(listOf(prevElem, elem)) }
                )
                prevElems += elem
            }
        }
    }
    check(size > 0) { "Size should be a positive number" }
    return when (size) {
        1 -> map { SingleTuple(it) }
        2 -> pairs()
        else -> sequence {
            val prevTuples = mutableMapOf<Int, MutableSet<Tuple<T>>>()
            for (i in 1 until size) prevTuples[i] = mutableSetOf()
            val (dropPart, sequence) = this@tuples.drop(size - 1)
            if (dropPart.size == size - 1) {
                dropPart.asSequence().map { SingleTuple(it) }.forEach { prevTuples[1]!!.add(it) }

                fun newTuples(tupleSize: Int, elem: T) = prevTuples[tupleSize]!!.asSequence()
                    .map { tuple -> ListTuple(tuple.asList().plus(elem)) }

                fun updateTuplesOfSpecificSizeWithElement(tupleSize: Int, elem: T) =
                    prevTuples[tupleSize]!!.addAll(newTuples(tupleSize - 1, elem))

                fun updateTuplesWithElement(elem: T) = (2 until size)
                    .forEach { tupleSize -> updateTuplesOfSpecificSizeWithElement(tupleSize, elem) }

                for (elem in dropPart)
                    updateTuplesWithElement(elem)
                sequence.forEach { elem ->
                    yieldAll(
                        newTuples(size - 1, elem)
                    )
                    prevTuples[1]!!.add(SingleTuple(elem))
                    updateTuplesWithElement(elem)
                }
            }
        }
    }
}

private data class DropResult<T>(val dropPart: List<T>, val sequence: Sequence<T>)

private fun <T> Sequence<T>.drop(size: Int): DropResult<T> {
    val it = iterator()
    val dropPart = mutableListOf<T>()
    for (i in 1..size) {
        if (it.hasNext()) {
            dropPart += it.next()
        }
    }
    return DropResult(
        dropPart,
        it.asSequence()
    )
}

interface Tuple<T> {
    operator fun get(index: Int): T
    fun asList(): List<T>
}

private class SingleTuple<T>(private val elem: T) : Tuple<T> {
    private val list by lazy { listOf(elem) }
    override fun get(index: Int): T = if (index == 0) elem else throw IndexOutOfBoundsException()
    override fun asList(): List<T> = list
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is Tuple<*> -> this.asList() == other.asList()
        else -> false
    }

    override fun hashCode(): Int = elem?.hashCode() ?: 0

}

private class ListTuple<T>(private val list: List<T>) : Tuple<T> {
    override fun get(index: Int): T = list[index]
    override fun asList(): List<T> = list
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is Tuple<*> -> this.asList() == other.asList()
        else -> false
    }

    override fun hashCode(): Int = list.hashCode()
}