package com.samuel.recipeApp.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.credentials.CredentialManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.samuel.recipeApp.ui.detail.RecipeDetailScreen
import com.samuel.recipeApp.ui.home.HomeScreen
import com.samuel.recipeApp.ui.login.LoginScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    isLoggedIn: Boolean,
    activity: ComponentActivity,
    credentialManager: CredentialManager,
    onLoginSuccess: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(
                activity = activity,
                credentialManager = credentialManager,
                onSuccess = {
                    onLoginSuccess()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            val auth = FirebaseAuth.getInstance()
            HomeScreen(
                currentUser = auth.currentUser,
                onRecipeClick = { recipeId ->
                    navController.navigate("detail/$recipeId")
                },
                onLogout = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
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