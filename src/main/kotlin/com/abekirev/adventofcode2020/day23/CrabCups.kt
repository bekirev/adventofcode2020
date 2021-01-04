package com.abekirev.adventofcode2020.day23

fun main() {
    partOne()
    partTwo()
}

private val input = "389547612".map { it.toString().toInt() }

private fun partOne() {
    val (circledLinkedList, head) = move(input, 100)
    println(
        circledLinkedList.removeAfter(1, input.size - 1).joinToString("")
    )
}

private fun partTwo() {
    val (circledLinkedList, head) = move(
        sequenceOf(input.asSequence(), generateSequence(input.maxOrNull()!! + 1, 1::plus))
        .flatten()
        .take(1_000_000)
        .toList(),
        10_000_000
    )
    println(circledLinkedList.removeAfter(1, 2).reduce(Int::times))
}

fun move(items: List<Cup>, movesCount: Int): Pair<CircledLinkedList<Cup>, Cup> {
    val circledLinkedList = CircledLinkedList(items)
    val min = items.minOrNull()!!
    val max = items.maxOrNull()!!
    fun destinationSequence(head: Cup): Sequence<Cup> = sequence {
        var cup = head
        while (true) {
            cup = when (cup) {
                min -> max
                else -> cup - 1
            }
            yield(cup)
        }
    }
    var head = input.first()
    repeat(movesCount) {
        val pickup = circledLinkedList.removeAfter(head, 3)
        val destination = destinationSequence(head).first(circledLinkedList::contains)
        circledLinkedList.placeAfter(destination, pickup)
        head = circledLinkedList.elemAfter(head)
    }
    return circledLinkedList to head
}

typealias Cup = Int

class CircledLinkedList<T>(items: List<T>) {
    private val elements: MutableMap<T, Node<T>>

    init {
        val elements = HashMap<T, Node<T>>()
        var first: Node<T>? = null
        var prev: Node<T>? = null
        for (item in items) {
            if (first == null) {
                first = Node(item)
                prev = first
            } else {
                prev = prev?.let { p ->
                    p.next = Node(item)
                    p.next
                }
            }
            prev?.let { p ->
                elements[p.value] = p
            }
        }
        if (first != null) {
            prev!!.next = first
        }
        this.elements = elements
    }

    private data class Node<T>(
        val value: T,
        var next: Node<T>? = null,
    )

    fun removeAfter(elem: T, count: Int): List<T> {
        return when (count) {
            0 -> emptyList()
            else -> {
                val node = elements[elem] ?: throw IllegalArgumentException("Element $elem is not present")
                val list = mutableListOf<T>()
                var i = 0
                while (i < count) {
                    val nextNode = node.next!!
                    list.add(nextNode.value)
                    elements.remove(nextNode.value)
                    node.next = nextNode.next
                    ++i
                }
                return list
            }
        }
    }

    fun placeAfter(elem: T, list: List<T>) {
        var node = elements[elem] ?: throw IllegalArgumentException("Element $elem is not present")
        for (item in list) {
            val nextNode = Node(item, node.next)
            elements[item] = nextNode
            node.next = nextNode
            node = nextNode
        }
    }

    fun elemAfter(elem: T): T =
        (elements[elem] ?: throw IllegalArgumentException("Element $elem is not present")).next!!.value

    operator fun contains(elem: T): Boolean = elements.containsKey(elem)
}
