package com.samuel.recipeApp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.recipeApp.data.api.Meal
import com.samuel.recipeApp.data.api.TranslatedMeal
import com.samuel.recipeApp.data.repository.RecipeRepository
import com.samuel.recipeApp.data.repository.Result
import com.samuel.recipeApp.utils.TranslationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecipeDetailUiState(
    val recipe: Meal? = null,
    val translatedMeal: TranslatedMeal? = null,
    val isLoading: Boolean = false,
    val isTranslating: Boolean = false,
    val error: String? = null
)

class RecipeDetailViewModel(
    private val repository: RecipeRepository = RecipeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadRecipeDetail(id: String) {
        viewModelScope.launch {
            repository.getRecipeById(id).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            recipe = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    /**
     * Translate recipe data menggunakan ML Kit
     */
    fun translateRecipe() {
        val recipe = _uiState.value.recipe ?: return
        val translationService = TranslationManager.getService() ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTranslating = true)

            try {
                // Translate ingredients
                val ingredients = recipe.getIngredients()
                val translatedIngredients = translationService.translateIngredients(ingredients)

                // Translate instructions
                val instructions = recipe.strInstructions ?: ""
                val translatedInstructions = if (instructions.isNotBlank()) {
                    translationService.translateInstructions(instructions)
                        .split("\n")
                        .filter { it.isNotBlank() }
                } else {
                    emptyList()
                }

                // Create TranslatedMeal object
                val translatedMeal = TranslatedMeal(
                    originalMeal = recipe,
                    translatedIngredients = translatedIngredients,
                    translatedInstructions = translatedInstructions
                )

                _uiState.value = _uiState.value.copy(
                    translatedMeal = translatedMeal,
                    isTranslating = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isTranslating = false,
                    error = "Translation failed: ${e.message}"
                )
            }
        }
    }

    /**
     * Clear translation
     */
    fun clearTranslation() {
        _uiState.value = _uiState.value.copy(translatedMeal = null)
    }
}