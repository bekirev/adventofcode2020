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