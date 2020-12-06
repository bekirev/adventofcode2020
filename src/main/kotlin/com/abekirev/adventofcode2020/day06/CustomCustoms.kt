package com.abekirev.adventofcode2020.day06

import com.abekirev.adventofcode2020.util.append
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    partOne()
    partTwo()
}

private val INPUT_PATH = Path.of("input", "day06", "input.txt")

private fun partOne() {
    println(
        INPUT_PATH.useLinesFromResource { lines ->
            lines.groups()
                .map(Group::questionsAnyAnsweredYes)
                .map(Collection<Question>::size)
                .sum()
        }
    )
}

private fun partTwo() {
    println(
        INPUT_PATH.useLinesFromResource { lines ->
            lines.groups()
                .map(Group::questionsAllAnsweredYes)
                .map(Collection<Question>::size)
                .sum()
        }
    )
}

private fun Sequence<String>.groups() = sequence {
    val tokens = this@groups
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