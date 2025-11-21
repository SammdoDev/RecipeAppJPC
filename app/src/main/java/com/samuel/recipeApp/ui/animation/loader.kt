package com.samuel.recipeApp.ui.animation

import androidx.compose.runtime.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*

@Composable
fun Loader(onFinish: () -> Unit) {

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(com.samuel.recipeApp.R.raw.loader)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 2,
        speed = 1.0f,
        restartOnPlay = false
    )

    LaunchedEffect(progress) {
        if (progress == 1f) onFinish()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(260.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "RecipeMe",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp
            )
        }
    }
}
