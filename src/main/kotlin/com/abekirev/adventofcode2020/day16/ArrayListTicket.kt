package com.abekirev.adventofcode2020.day16

class ArrayListTicket(
    private val arrayList: ArrayList<Int>
) : Ticket {
    override val numbers: List<Int>
        get() = arrayList
}