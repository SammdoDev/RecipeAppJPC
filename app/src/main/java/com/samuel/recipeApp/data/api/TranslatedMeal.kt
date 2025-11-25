package com.samuel.recipeApp.data.api

data class TranslatedMeal(
    val originalMeal: Meal,
    val translatedIngredients: List<Pair<String, String>>? = null,
    val translatedInstructions: List<String>? = null
) {
    fun getIngredients(useTranslation: Boolean): List<Pair<String, String>> {
        return if (useTranslation && translatedIngredients != null) {
            translatedIngredients
        } else {
            originalMeal.getIngredients()
        }
    }

    fun getInstructions(useTranslation: Boolean): List<String> {
        return if (useTranslation && translatedInstructions != null) {
            translatedInstructions
        } else {
            originalMeal.getInstructionSteps()
        }
    }
}