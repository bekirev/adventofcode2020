package com.abekirev.adventofcode2020.day16

interface TicketParser {
    fun parse(string: String): Ticket
}