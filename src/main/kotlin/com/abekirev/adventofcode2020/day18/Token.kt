package com.abekirev.adventofcode2020.day18

sealed class Token
data class NumberToken(val number: Long) : Token()
object LeftParenthesisToken : Token()
object RightParenthesisToken : Token()
sealed class Operation : Token()
object AddToken : Operation()
object MultiplyToken : Operation()
