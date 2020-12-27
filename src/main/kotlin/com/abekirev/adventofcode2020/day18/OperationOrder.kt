package com.abekirev.adventofcode2020.day18

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path
import java.util.Deque
import java.util.LinkedList
import java.util.Queue
import java.util.Stack

fun main() {
    partOne()
}

private fun partOne() {
    println(
        Path.of("input", "day18", "input.txt").useLinesFromResource { lines ->
            lines
                .map(String::tokenized)
                .map(List<Token>::toExpressionEqualPriority)
                .map(Expression::eval)
                .sum()
        }
    )
}

fun List<Token>.toExpressionEqualPriority(): Expression =
    toExpression {
        when (this) {
            is NumberToken -> 1
            LeftParenthesisToken -> 0
            RightParenthesisToken -> 0
            AddToken -> 2
            MultiplyToken -> 2
        }
    }

fun List<Token>.toExpression(priority: Token.() -> Int): Expression {
    fun List<Token>.toPolishNotation(): Queue<Token> {
        val operationStack = Stack<Token>()
        val outputQueue: Deque<Token> = LinkedList()
        for (token in this) {
            when (token) {
                is NumberToken -> outputQueue.addFirst(token)
                is Operation -> {
                    while (operationStack.isNotEmpty() && operationStack.peek().priority() >= token.priority())
                        outputQueue.addFirst(operationStack.pop())
                    operationStack.push(token)
                }
                is LeftParenthesisToken -> operationStack.push(token)
                is RightParenthesisToken -> {
                    while (operationStack.peek() != LeftParenthesisToken)
                        outputQueue.addFirst(operationStack.pop())
                    operationStack.pop()
                }
            }
        }
        while (operationStack.isNotEmpty()) {
            outputQueue.addFirst(operationStack.pop())
        }
        return outputQueue
    }

    fun Queue<Token>.toExpression(): Expression = when (val token = remove()) {
        is NumberToken -> NumberExpression(token.number)
        AddToken -> AddExpression(
            rightExpression = toExpression(),
            leftExpression = toExpression()
        )
        MultiplyToken -> MultiplyExpression(
            rightExpression = toExpression(),
            leftExpression = toExpression()
        )
        else -> throw IllegalStateException()
    }
    return toPolishNotation().toExpression()
}

fun String.tokenized(): List<Token> =
    replace("(", "( ")
        .replace(")", " )")
        .split(Regex("""\s+"""))
        .map {
            when (it) {
                "(" -> LeftParenthesisToken
                ")" -> RightParenthesisToken
                "+" -> AddToken
                "*" -> MultiplyToken
                else -> NumberToken(it.toLong())
            }
        }
