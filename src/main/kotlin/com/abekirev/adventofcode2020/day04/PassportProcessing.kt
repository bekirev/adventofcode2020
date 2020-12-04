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
    partTwo()
}

private fun partOne() {
    printResult(
        RequiredFieldsPassportValidator(
            FieldType.values().filterNot(COUNTRY_ID::equals).toSet()
        )
    )
}

private fun partTwo() {
    printResult(
        FieldCheckPassportValidator(
            BIRTH_YEAR to RangeFieldValueValidator(1920..2002),
            ISSUE_YEAR to RangeFieldValueValidator(2010..2020),
            EXPIRATION_YEAR to RangeFieldValueValidator(2020..2030),
            HEIGHT to RangeWithPostfixDeterminerFieldValueValidator(
                "cm" to 150..193,
                "in" to 59..76
            ),
            HAIR_COLOR to RegexValueValidator(Regex("""#([0-9]|[a-f]){6}""")),
            EYE_COLOR to OneOfValueValidator("amb", "blu", "brn", "gry", "grn", "hzl", "oth"),
            PASSPORT_ID to RegexValueValidator(Regex("""\d{9}"""))
        )
    )
}

private fun printResult(passportValidator: PassportValidator) {
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

private class FieldCheckPassportValidator private constructor(
    private val fieldValueValidators: Map<FieldType, FieldValueValidator>,
) : PassportValidator {
    constructor(vararg fieldValueValidators: Pair<FieldType, FieldValueValidator>) : this(fieldValueValidators.toMap())

    override fun validate(passport: Passport): Boolean =
        fieldValueValidators.asSequence()
            .all { (type, fieldValueValidator) ->
                fieldValueValidator.validate(passport[type])
            }
}

private interface FieldValueValidator {
    fun validate(value: FieldValue?): Boolean
}

private class RangeFieldValueValidator(
    private val range: IntRange,
) : FieldValueValidator {
    override fun validate(value: FieldValue?): Boolean = value?.toIntOrNull()?.let(range::contains) ?: false
}

private typealias Determiner = String

private class RangeWithPostfixDeterminerFieldValueValidator private constructor(
    private val rules: Map<Determiner, IntRange>,
) : FieldValueValidator {
    constructor(vararg rules: Pair<Determiner, IntRange>) : this(rules.toMap())

    override fun validate(value: FieldValue?): Boolean =
        if (value != null) {
            rules.asSequence()
                .filter { (determiner, _) ->
                    value.endsWith(determiner)
                }
                .any { (determiner, range) ->
                    value.dropLast(determiner.length).toIntOrNull()?.let(range::contains) ?: false
                }
        } else {
            false
        }
}

private class RegexValueValidator(
    private val regex: Regex,
) : FieldValueValidator {
    override fun validate(value: FieldValue?): Boolean =
        value?.matches(regex) ?: false
}

private class OneOfValueValidator private constructor(
    private val allowedValues: Set<FieldValue>,
) : FieldValueValidator {
    constructor(vararg values: FieldValue) : this(values.toSet())

    override fun validate(value: FieldValue?): Boolean =
        value in allowedValues
}

private fun input() = sequence<Passport> {
    val tokens = Path.of("input", "day04", "input.txt")
        .linesFromResource()
        .asSequence()
        .flatMap { it.split(" ") }
        .map(String::toToken)
        .append(BlackLineToken)
    val fields = mutableMapOf<FieldType, FieldValue>()
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

private typealias FieldValue = String

private interface Passport {
    operator fun get(fieldType: FieldType): FieldValue?
}

private class MapPassport(
    private val fields: Map<FieldType, FieldValue>,
) : Passport {
    override fun get(fieldType: FieldType): FieldValue? =
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
