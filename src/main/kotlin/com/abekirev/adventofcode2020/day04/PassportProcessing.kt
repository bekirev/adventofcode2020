package com.abekirev.adventofcode2020.day04

import com.abekirev.adventofcode2020.day04.FieldType.BIRTH_YEAR
import com.abekirev.adventofcode2020.day04.FieldType.COUNTRY_ID
import com.abekirev.adventofcode2020.day04.FieldType.EXPIRATION_YEAR
import com.abekirev.adventofcode2020.day04.FieldType.EYE_COLOR
import com.abekirev.adventofcode2020.day04.FieldType.HAIR_COLOR
import com.abekirev.adventofcode2020.day04.FieldType.HEIGHT
import com.abekirev.adventofcode2020.day04.FieldType.ISSUE_YEAR
import com.abekirev.adventofcode2020.day04.FieldType.PASSPORT_ID
import com.abekirev.adventofcode2020.util.linesFromResource
import java.nio.file.Path
import kotlin.streams.asSequence

fun main() {
    partOne()
}

private fun partOne() {
    val passportValidator = RequiredFieldsPassportValidator(
        FieldType.values().filterNot(COUNTRY_ID::equals).toSet()
    )
    println(
        input()
            .count(passportValidator::validate)
    )
}

private interface PassportValidator {
    fun validate(passport: Passport): Boolean
}

private class RequiredFieldsPassportValidator(
    private val requiredFields: Set<FieldType>,
) : PassportValidator {
    override fun validate(passport: Passport): Boolean =
        requiredFields
            .map(passport::get)
            .all { it != null }
}

private fun input() = sequence<Passport> {
    val tokens = Path.of("input", "day04", "input.txt")
        .linesFromResource()
        .asSequence()
        .flatMap { it.split(" ") }
        .map(String::toToken)
        .append(BlackLineToken)
    val fields = mutableMapOf<FieldType, String>()
    for (token in tokens) {
        when (token) {
            is FieldToken -> fields[token.fieldType] = token.value
            BlackLineToken -> {
                if (fields.isNotEmpty()) {
                    yield(MapPassport(fields.toMap()))
                    fields.clear()
                }
            }
        }
    }
}

private fun <T> Sequence<T>.append(elem: T): Sequence<T> = sequence {
    for (value in this@append)
        yield(value)
    yield(elem)
}

private sealed class Token
private data class FieldToken(val fieldType: FieldType, val value: String) : Token()
private object BlackLineToken : Token()

private const val KEY_VALUE_SEPARATOR = ":"
private fun String.toToken(): Token {
    return if (contains(KEY_VALUE_SEPARATOR))
        split(KEY_VALUE_SEPARATOR).let { (key, value) ->
            FieldToken(
                key.toFieldType(),
                value
            )
        }
    else
        BlackLineToken
}

private interface Passport {
    operator fun get(fieldType: FieldType): String?
}

private class MapPassport(
    private val fields: Map<FieldType, String>,
) : Passport {
    override fun get(fieldType: FieldType): String? =
        fields[fieldType]
}

private enum class FieldType {
    BIRTH_YEAR,
    ISSUE_YEAR,
    EXPIRATION_YEAR,
    HEIGHT,
    HAIR_COLOR,
    EYE_COLOR,
    PASSPORT_ID,
    COUNTRY_ID,
    ;
}

private fun String.toFieldType(): FieldType = when (this) {
    "byr" -> BIRTH_YEAR
    "iyr" -> ISSUE_YEAR
    "eyr" -> EXPIRATION_YEAR
    "hgt" -> HEIGHT
    "hcl" -> HAIR_COLOR
    "ecl" -> EYE_COLOR
    "pid" -> PASSPORT_ID
    "cid" -> COUNTRY_ID
    else -> throw IllegalArgumentException("Unknown field type $this")
}
