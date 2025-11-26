package com.samuel.recipeApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.credentials.CredentialManager
import com.google.firebase.auth.FirebaseAuth
import com.samuel.recipeApp.ui.navigation.AppNavGraph
import com.samuel.recipeApp.ui.theme.RecipeAppTheme
import com.samuel.recipeApp.utils.TranslationManager

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Credential Manager for Google Sign-In
        credentialManager = CredentialManager.create(this)

        // Initialize Translation Manager
        TranslationManager.initialize(this)

        setContent {
            RecipeAppTheme {
                var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }

                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavGraph(
                        isLoggedIn = isLoggedIn,
                        activity = this@MainActivity,
                        credentialManager = credentialManager,
                        onLoginSuccess = {
                            isLoggedIn = true
                        }
                    )
                }
            }
        }
    }
}