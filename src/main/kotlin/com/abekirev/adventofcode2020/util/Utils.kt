package com.abekirev.adventofcode2020.util

import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

object PathFinder {
    private fun uriFromResources(strPath: String): URI {
        return javaClass.classLoader.getResource(strPath)?.toURI() ?: throw error("Resource not found: $strPath")
    }

    fun fromResources(strPath: String): Path {
        return Paths.get(
            uriFromResources(strPath)
        )
    }
}

private fun String.linesFromResource(): Stream<String> = Files.lines(PathFinder.fromResources(this))!!

fun Path.linesFromResource(): Stream<String> = this.toString().linesFromResource()
fun <T> Sequence<T>.append(elem: T): Sequence<T> = sequence {
    for (value in this@append)
        yield(value)
    yield(elem)
}