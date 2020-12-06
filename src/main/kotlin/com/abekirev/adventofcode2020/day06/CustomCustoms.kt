package com.abekirev.adventofcode2020.day06

import com.abekirev.adventofcode2020.util.append
import com.abekirev.adventofcode2020.util.linesFromResource
import java.nio.file.Path
import kotlin.streams.asSequence

fun main() {
    partOne()
    partTwo()
}

private fun partOne() {
    println(
        input()
            .map(Group::questionsAnyAnsweredYes)
            .map(Collection<Question>::size)
            .sum()
    )
}

private fun partTwo() {
    println(
        input()
            .map(Group::questionsAllAnsweredYes)
            .map(Collection<Question>::size)
            .sum()
    )
}

private fun input() = sequence {
    val tokens = Path.of("input", "day06", "input.txt")
        .linesFromResource()
        .asSequence()
        .map(String::toToken)
        .append(BlackLineToken)
    val questions = mutableListOf<Set<Question>>()
    for (token in tokens) {
        when (token) {
            is QuestionsToken -> questions += token.questionsWithYesAnswer
            BlackLineToken -> {
                if (questions.isNotEmpty()) {
                    yield(Group(questions))
                    questions.clear()
                }
            }
        }
    }
}

private class Group private constructor(private val questionsWithYesAnswer: Collection<Set<Question>>) {
    constructor(questionsWithYesAnswer: Iterable<Set<Question>>) : this(questionsWithYesAnswer.toList())

    fun questionsAnyAnsweredYes(): Set<Question> =
        questionsWithYesAnswer.flatMapTo(mutableSetOf()) { it }

    fun questionsAllAnsweredYes(): Set<Question> =
        questionsWithYesAnswer.reduce { acc, questions -> acc.intersect(questions) }
}

private sealed class Token
private data class QuestionsToken(val questionsWithYesAnswer: Set<Question>) : Token()
private object BlackLineToken : Token()

private typealias Question = Char

private fun String.toToken() =
    if (isNotEmpty()) QuestionsToken(toQuestions())
    else BlackLineToken

private fun String.toQuestions() = toSet()