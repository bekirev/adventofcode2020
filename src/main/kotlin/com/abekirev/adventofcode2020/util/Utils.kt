package com.abekirev.adventofcode2020.util

import java.io.InputStream
import java.nio.file.Path

private object ResourcesUtils {
    fun getResourceAsStream(name: String): InputStream =
        javaClass.classLoader.getResourceAsStream(name) ?: throw IllegalArgumentException("No resource with name: $name")
}

fun <T> Path.useLinesFromResource(block: (Sequence<String>) -> T): T =
    ResourcesUtils.getResourceAsStream(this.toString()).bufferedReader()
        .useLines(block)

fun <T> Sequence<T>.append(elem: T): Sequence<T> = sequence {
    for (value in this@append)
        yield(value)
    yield(elem)
}

inline fun <reified T> Sequence<T>.pair(): Sequence<Pair<T, T>> = sequence {
    val prevArr: Array<T?> = arrayOf(null)
    for (elem in this@pair) {
        val prev = prevArr[0]
        if (prev != null) {
            yield(prev to elem)
            prevArr[0] = elem
        } else {
            prevArr[0] = elem
        }
    }
}

inline fun <T, K, V, C : MutableCollection<V>, M : MutableMap<in K, C>> Sequence<T>.groupByTo(
    destination: M,
    collectionProvider: () -> C,
    keySelector: (T) -> K,
    valueTransform: (T) -> V,
): M {
    for (element in this) {
        val key = keySelector(element)
        val list = destination.getOrPut(key) { collectionProvider() }
        list.add(valueTransform(element))
    }
    return destination
}

fun Sequence<Long>.product(): Long? = this.reduceOrNull(Long::times)