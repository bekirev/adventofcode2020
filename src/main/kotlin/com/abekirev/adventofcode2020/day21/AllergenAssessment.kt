package com.abekirev.adventofcode2020.day21

import com.abekirev.adventofcode2020.util.findCoverage
import com.abekirev.adventofcode2020.util.useLinesFromResource
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.streams.asStream

fun main() {
    partOne()
    partTwo()
}

private fun partOne() =
    println(
        Path.of("input", "day21", "input.txt").useLinesFromResource { lines ->
            lines.countIngredientsEntriesWithoutAllergens()
        }
    )

private fun partTwo() =
    println(
        Path.of("input", "day21", "input.txt").useLinesFromResource { lines ->
            findCoverage(lines.food().ingredientsByAllergens())
                .entries
                .sortedBy { it.key }
                .joinToString(",") { it.value }
        }
    )

fun Sequence<String>.countIngredientsEntriesWithoutAllergens(): Int {
    val food = food().toList()
    val ingredientsWithAllergens = food.asSequence().ingredientsByAllergens().ingredientsWithAllergens().toSet()
    return food.asSequence().countNonAllergicIngredientsEntries(ingredientsWithAllergens)
}

fun Sequence<String>.food(): Sequence<Food> =
    map(FoodParserImpl(FoodStringSplitterImpl, IngredientsParserImpl, AllergenParserImpl)::parse)

typealias Ingredient = String
typealias Allergen = String

data class Food(
    val ingredients: Set<Ingredient>,
    val allergens: Set<Allergen>,
) {
    constructor(
        ingredients: Collection<Ingredient>,
        allergens: Collection<Allergen>,
    ) : this(ingredients.toSet(), allergens.toSet())
}

interface IngredientParser {
    fun parse(str: String): Sequence<Ingredient>
}

object IngredientsParserImpl : IngredientParser {
    override fun parse(str: String): Sequence<Ingredient> =
        if (str.isEmpty()) emptySequence()
        else str.splitToSequence(" ")
}

interface AllergenParser {
    fun parse(str: String): Sequence<Allergen>
}

object AllergenParserImpl : AllergenParser {
    override fun parse(str: String): Sequence<Allergen> =
        if (str.isEmpty()) emptySequence()
        else str.splitToSequence(", ")
}

interface FoodStringSplitter {
    data class IngredientsAllergensPair(
        val ingredientsStr: String,
        val allergensStr: String,
    )

    fun split(str: String): IngredientsAllergensPair
}

object FoodStringSplitterImpl : FoodStringSplitter {
    override fun split(str: String): FoodStringSplitter.IngredientsAllergensPair {
        val (ingredientsStr, allergensStr) = str.split(" (contains ")
        return FoodStringSplitter.IngredientsAllergensPair(
            ingredientsStr,
            allergensStr.dropLast(1)
        )
    }
}

interface FoodParser {
    fun parse(str: String): Food
}

class FoodParserImpl(
    private val foodStringSplitter: FoodStringSplitter,
    private val ingredientParser: IngredientParser,
    private val allergenParser: AllergenParser,
) : FoodParser {
    override fun parse(str: String): Food {
        val (ingredientsStr, allergensStr) = foodStringSplitter.split(str)
        return Food(
            ingredientParser.parse(ingredientsStr).toSet(),
            allergenParser.parse(allergensStr).toSet()
        )
    }
}

fun Sequence<Food>.ingredientsByAllergens(): Map<Allergen, Set<Ingredient>> =
    asStream()
        .flatMap { (ingredients, allergens) ->
            allergens.stream().map { allergen -> allergen to ingredients }
        }
        .collect(
            Collectors.toMap(
                { it.first },
                { it.second },
                { first, second -> first.intersect(second) },
            )
        )

fun Map<Allergen, Set<Ingredient>>.ingredientsWithAllergens(): Sequence<Ingredient> =
    asSequence().flatMap { it.value.asSequence() }.distinct()

fun Sequence<Food>.countNonAllergicIngredientsEntries(ingredientsWithAllergens: Set<Ingredient>): Int =
    flatMap { it.ingredients.asSequence() }
        .count { ingredient -> ingredient !in ingredientsWithAllergens }
