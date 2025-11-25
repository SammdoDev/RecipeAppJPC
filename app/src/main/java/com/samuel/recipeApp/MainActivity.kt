package com.samuel.recipeApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.samuel.recipeApp.ui.animation.Loader
import com.samuel.recipeApp.ui.navigation.AppNavGraph
import com.samuel.recipeApp.utils.TranslationManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TranslationManager.initialize(this)

        setContent {

            var showSplash by remember { mutableStateOf(true) }

            Surface(color = MaterialTheme.colorScheme.background) {
                if (showSplash) {
                    Loader(onFinish = { showSplash = false })
                } else {
                    AppNavGraph()
                }
            }
        }
    }
}
