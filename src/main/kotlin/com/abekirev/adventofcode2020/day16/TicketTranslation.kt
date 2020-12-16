package com.abekirev.adventofcode2020.day16

import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path

fun main() {
    val numberChecks = Path.of("input", "day16", "rules.txt").useLinesFromResource { lines ->
        lines
            .map(RuleParserImpl::parse)
            .map(Rule::toNumberCheck)
            .toList()
    }
    partOne(numberChecks)
}

private fun partOne(numberChecks: List<NumberCheck>) {
    println(
        Path.of("input", "day16", "nearby_tickets.txt").useLinesFromResource { lines ->
            lines
                .map(TicketParserImpl::parse)
                .ticketScanningErrorRate(numberChecks)
        }
    )
}

private fun Sequence<Ticket>.ticketScanningErrorRate(numberChecks: List<NumberCheck>): Int =
    fold(0) { errorRate, ticket ->
        errorRate + ticket.numbers.asSequence()
            .filter { number ->
                number.notCompliesWithAnyNumberCheck(numberChecks)
            }
            .sum()
    }

private fun Int.notCompliesWithAnyNumberCheck(numberChecks: List<NumberCheck>): Boolean =
    numberChecks.all { numberCheck ->
        this notCompliesWith numberCheck
    }

fun Rule.toNumberCheck(): NumberCheck {
    return OrNumberCheck(
        RangeNumberCheck(firstRange),
        RangeNumberCheck(secondRange),
    )
}
