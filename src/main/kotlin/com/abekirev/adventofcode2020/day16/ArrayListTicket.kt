package com.abekirev.adventofcode2020.day16

class ArrayListTicket(
    private val arrayList: ArrayList<Number>
) : Ticket {
    override val numbers: List<Number>
        get() = arrayList
}