package com.samuel.recipeApp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.samuel.recipeApp.ui.detail.RecipeDetailScreen
import com.samuel.recipeApp.ui.home.HomeScreen

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate("detail/$recipeId")
                }
            )
        }

        composable("detail/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            RecipeDetailScreen(
                recipeId = recipeId,
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}