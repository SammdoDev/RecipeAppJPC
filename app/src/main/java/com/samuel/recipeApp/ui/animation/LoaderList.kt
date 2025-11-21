package com.samuel.recipeApp.ui.animation

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import com.airbnb.lottie.compose.*

@Composable
fun LoaderList() {

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(com.samuel.recipeApp.R.raw.loader)
    )

    // Loop animasi selamanya (bebas untuk loading data)
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1.0f
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Loading Recipes...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
