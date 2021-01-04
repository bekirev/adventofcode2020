package com.abekirev.adventofcode2020.day21

import com.abekirev.adventofcode2020.day21.FoodStringSplitter.IngredientsAllergensPair
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class AllergenAssessmentTest : ShouldSpec({
    should("parse empty ingredients") {
        IngredientsParserImpl.parse("").toSet() shouldBe emptySet()
    }
    should("parse single ingredient") {
        IngredientsParserImpl.parse("abs").toSet() shouldBe setOf("abs")
    }
    should("parse multiple ingredients") {
        IngredientsParserImpl.parse("malta m das").toSet() shouldBe setOf("malta", "m", "das")
    }
    should("parse empty allergens") {
        AllergenParserImpl.parse("").toSet() shouldBe emptySet()
    }
    should("parse single allergens") {
        AllergenParserImpl.parse("jug").toSet() shouldBe setOf("jug")
    }
    should("parse multiple allergens") {
        AllergenParserImpl.parse("haha, ga, phi").toSet() shouldBe setOf("haha", "ga", "phi")
    }
    should("split string to ingredients and allergens") {
        FoodStringSplitterImpl.split("g v m (contains b, kj)") shouldBe IngredientsAllergensPair("g v m", "b, kj")
    }
    should("create food with ingredients and allergens") {
        val food = Food(
            ingredients = listOf("ba", "dad"),
            allergens = listOf("l", "ha", "me")
        )
        food.ingredients shouldBe setOf("ba", "dad")
        food.allergens shouldBe setOf("l", "ha", "me")
    }
    should("parse ingredients and allergens") {
        val inputStr = "jh n (contains ga, ma)"
        val foodStringSplitter = mock<FoodStringSplitter>() {
            on { split(any()) } doReturn IngredientsAllergensPair("jh n", "ga, ma")
        }
        val ingredientsParser = mock<IngredientParser>() {
            on { parse(any()) } doReturn sequenceOf("jh", "n")
        }
        val allergenParser = mock<AllergenParser>() {
            on { parse(any()) } doReturn sequenceOf("ga", "ma")
        }
        val foodParser = FoodParserImpl(
            foodStringSplitter,
            ingredientsParser,
            allergenParser
        )
        val food = foodParser.parse(inputStr)
        verify(foodStringSplitter, times(1)).split(inputStr)
        verify(ingredientsParser, times(1)).parse("jh n")
        verify(allergenParser, times(1)).parse("ga, ma")
        food shouldBe Food(
            ingredients = setOf("jh", "n"),
            allergens = setOf("ga", "ma")
        )
    }
    should("return ingredients containing possibly containing allergen") {
        val ingredientsByAllergens = sequenceOf(
            Food(
                ingredients = setOf("a", "b", "c", "d"),
                allergens = setOf("dairy", "fish"),
            ),
            Food(
                ingredients = setOf("e", "f", "g", "a"),
                allergens = setOf("dairy"),
            ),
            Food(
                ingredients = setOf("c", "f"),
                allergens = setOf("soy"),
            ),
            Food(
                ingredients = setOf("c", "a", "g"),
                allergens = setOf("fish"),
            ),
        ).ingredientsByAllergens()
        ingredientsByAllergens["dairy"] shouldBe setOf("a")
        ingredientsByAllergens["fish"] shouldBe setOf("a", "c")
        ingredientsByAllergens["soy"] shouldBe setOf("c", "f")
    }
    should("return ingredients with allergens") {
        mapOf(
            "dairy" to setOf("a"),
            "fish" to setOf("a", "c"),
            "soy" to setOf("c", "f"),
        ).ingredientsWithAllergens().toSet() shouldBe setOf("a", "c", "f")
    }
    should("count ingredients entries without allergens") {
        sequenceOf(
            Food(
                ingredients = setOf("a", "b", "c", "d"),
                allergens = setOf("dairy", "fish"),
            ),
            Food(
                ingredients = setOf("e", "f", "g", "a"),
                allergens = setOf("dairy"),
            ),
            Food(
                ingredients = setOf("c", "f"),
                allergens = setOf("soy"),
            ),
            Food(
                ingredients = setOf("c", "a", "g"),
                allergens = setOf("fish"),
            ),
        ).countNonAllergicIngredientsEntries(setOf("a", "c", "f")) shouldBe 5
    }
    should("count ingredients entries without allergens from raw input") {
        sequenceOf(
            "mxmxvkd kfcds sqjhc nhms (contains dairy, fish)",
            "trh fvjkl sbzzf mxmxvkd (contains dairy)",
            "sqjhc fvjkl (contains soy)",
            "sqjhc mxmxvkd sbzzf (contains fish)",
        ).countIngredientsEntriesWithoutAllergens() shouldBe 5
    }
})