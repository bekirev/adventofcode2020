package com.abekirev.adventofcode2020.day16

import com.abekirev.adventofcode2020.util.product
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    val rules = Path.of("input", "day16", "rules.txt").useLinesFromResource { lines ->
        lines
            .map(RuleParserImpl::parse)
            .toList()
    }
    val numberChecks = rules.map(Rule::toNumberCheck)
    partOne(numberChecks)
    partTwo(rules)
}

private fun partOne(numberChecks: List<NumberCheck>) =
    println(
        Path.of("input", "day16", "nearby_tickets.txt").useLinesFromResource { lines ->
            lines
                .map(TicketParserImpl::parse)
                .ticketScanningErrorRate(numberChecks)
        }
    )

private fun partTwo(rules: List<Rule>) {
    val fieldNumberCheckMap = rules.map { it.field to it.toNumberCheck() }.toMap()
    val possibleVariants = findPossibleFields(fieldNumberCheckMap)
    val indexesWithFieldsStartWithDeparture = findCoverage(possibleVariants)
        .filter { entry ->
            entry.value.startsWith("departure")
        }
        .map { entry ->
            entry.key
        }
        .toSet()
    val myTicket = Path.of("input", "day16", "my_ticket.txt").useLinesFromResource { lines ->
        TicketParserImpl.parse(lines.first())
    }
    println(
        indexesWithFieldsStartWithDeparture
            .asSequence()
            .map(myTicket.numbers::get)
            .map(Int::toLong)
            .product()
    )
}

private fun findPossibleFields(fieldNumberCheckMap: Map<String, NumberCheck>) =
    Path.of("input", "day16", "nearby_tickets.txt").useLinesFromResource { lines ->
        lines
            .toList()
            .asSequence()
            .map(TicketParserImpl::parse)
            .filterValid(NumberChecksTicketValidator(fieldNumberCheckMap.values))
            .map { ticket ->
                ticket.numbers
                    .asSequence()
                    .mapIndexed { index, number ->
                        index to fieldNumberCheckMap.asSequence()
                            .filter { (_, numberCheck) ->
                                number compliesWith numberCheck
                            }
                            .map { entry -> entry.key }
                            .toSet()
                    }
                    .toMap()
            }
            .reduce { a, b ->
                merge(a, b) { _, valueA, valueB ->
                    if (valueA.isNullOrEmpty() || valueB.isNullOrEmpty()) emptySet()
                    else valueA intersect valueB
                }
            }
    }

private fun <K : Any, V : Any> merge(
    mapA: Map<K, V>,
    mapB: Map<K, V>,
    mergeFunc: (key: K, valueA: V?, valueB: V?) -> V?,
): Map<K, V> =
    sequenceOf(
        mapA.keys.asSequence(),
        mapB.keys.asSequence(),
    )
        .flatten()
        .distinct()
        .mapNotNull { key ->
            mergeFunc(key, mapA[key], mapB[key])?.let { mergedValue ->
                key to mergedValue
            }
        }
        .toMap()

private fun Sequence<Ticket>.ticketScanningErrorRate(numberChecks: Collection<NumberCheck>): Int =
    fold(0) { errorRate, ticket ->
        errorRate + ticket.numbers.asSequence()
            .filter { number ->
                number.notCompliesWithAllNumberCheck(numberChecks)
            }
            .sum()
    }

private fun Number.notCompliesWithAllNumberCheck(numberChecks: Collection<NumberCheck>): Boolean =
    numberChecks.all { numberCheck ->
        this notCompliesWith numberCheck
    }

private interface TicketValidator {
    fun validate(ticket: Ticket): Boolean
}

private class NumberChecksTicketValidator(
    private val numberChecks: Collection<NumberCheck>,
) : TicketValidator {
    override fun validate(ticket: Ticket): Boolean =
        !ticket.numbers.any { number ->
            number.notCompliesWithAllNumberCheck(numberChecks)
        }
}

private fun Sequence<Ticket>.filterValid(ticketValidator: TicketValidator): Sequence<Ticket> =
    filter(ticketValidator::validate)

fun Rule.toNumberCheck(): NumberCheck {
    return OrNumberCheck(
        RangeNumberCheck(firstRange),
        RangeNumberCheck(secondRange),
    )
}

fun <T, C> findCoverage(
    possibleVariants: Map<T, Set<C>>,
): Map<T, C> {
    tailrec fun findCoverage(
        variants: MutableMap<T, MutableSet<C>>,
        result: MutableMap<T, C>,
    ): MutableMap<T, C> = when (variants.size) {
        0 -> result
        else -> {
            val minEntry = variants.minByOrNull {
                it.value.size
            }!!
            val c = minEntry.value.first()
            variants.remove(minEntry.key)
            variants.values.forEach { it.remove(c) }
            result[minEntry.key] = c
            findCoverage(
                variants,
                result,
            )
        }
    }
    return findCoverage(
        possibleVariants.mapValuesTo(mutableMapOf()) { (_, value) ->
            value.toMutableSet()
        },
        mutableMapOf(),
    )
}