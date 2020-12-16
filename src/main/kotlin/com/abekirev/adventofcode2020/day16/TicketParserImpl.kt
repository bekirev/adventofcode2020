package com.abekirev.adventofcode2020.day16

object TicketParserImpl : TicketParser {
    override fun parse(string: String): Ticket =
        ArrayListTicket(
            string.splitToSequence(",")
                .map(String::toInt)
                .toCollection(ArrayList())
        )
}