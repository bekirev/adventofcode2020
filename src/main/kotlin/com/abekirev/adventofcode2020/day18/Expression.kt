package com.abekirev.adventofcode2020.day18

sealed class Expression {
    abstract fun eval(): Long
}

class NumberExpression(
    private val value: Long,
) : Expression() {
    override fun eval(): Long = value
}

class AddExpression(
    private val leftExpression: Expression,
    private val rightExpression: Expression,
) : Expression() {
    override fun eval(): Long =
        leftExpression.eval() + rightExpression.eval()
}

class MultiplyExpression(
    private val leftExpression: Expression,
    private val rightExpression: Expression,
) : Expression() {
    override fun eval(): Long =
        leftExpression.eval() * rightExpression.eval()
}
