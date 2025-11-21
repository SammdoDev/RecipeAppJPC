package com.samuel.recipeApp.data.repository

import com.samuel.recipeApp.data.api.Meal
import com.samuel.recipeApp.data.api.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class RecipeRepository {

    suspend fun searchRecipes(query: String): Flow<Result<List<Meal>>> = flow {
        emit(Result.Loading)
        try {
            val response = RetrofitInstance.api.searchMeals(query)
            if (response.meals != null) {
                emit(Result.Success(response.meals))
            } else {
                emit(Result.Error("No recipes found"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    suspend fun getRecipeById(id: String): Flow<Result<Meal>> = flow {
        emit(Result.Loading)
        try {
            val response = RetrofitInstance.api.getMealById(id)
            if (response.meals?.isNotEmpty() == true) {
                emit(Result.Success(response.meals.first()))
            } else {
                emit(Result.Error("Recipe not found"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    suspend fun getRandomRecipes(count: Int = 10): Flow<Result<List<Meal>>> = flow {
        emit(Result.Loading)
        try {
            val meals = mutableListOf<Meal>()
            repeat(count) {
                val response = RetrofitInstance.api.getRandomMeal()
                response.meals?.firstOrNull()?.let { meals.add(it) }
            }
            emit(Result.Success(meals))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    suspend fun getRecipesByCategory(category: String): Flow<Result<List<Meal>>> = flow {
        emit(Result.Loading)
        try {
            val response = RetrofitInstance.api.getMealsByCategory(category)
            if (response.meals != null) {
                emit(Result.Success(response.meals))
            } else {
                emit(Result.Error("No recipes found"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }
}