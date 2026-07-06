package com.iti.presentation.screens.splash

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iti.presentation.screens.splash.components.SplashLogo
import com.iti.presentation.screens.splash.components.SplashTagline
import com.iti.presentation.util.Constants
import kotlinx.coroutines.delay

import androidx.compose.ui.res.stringResource
import com.iti.presentation.R

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onAnimationComplete: () -> Unit = {}
) {
    var startAnimation by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) Constants.SPLASH_LOGO_SCALE_TARGET else Constants.SPLASH_LOGO_SCALE_START,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(Constants.SPLASH_LOGO_ALPHA_DURATION),
        label = "logoAlpha"
    )

    val dividerWidth by animateDpAsState(
        targetValue = if (startAnimation) Constants.SPLASH_DIVIDER_WIDTH_TARGET.dp else 0.dp,
        animationSpec = tween(Constants.SPLASH_DIVIDER_DURATION, delayMillis = Constants.SPLASH_DIVIDER_DELAY),
        label = "dividerWidth"
    )

    var tagline1Text by remember { mutableStateOf("") }
    var tagline2Text by remember { mutableStateOf("") }

    val tagline1 = stringResource(id = R.string.splash_tagline_1)
    val tagline2 = stringResource(id = R.string.splash_tagline_2)

    LaunchedEffect(key1 = tagline1, key2 = tagline2) {
        startAnimation = true

        delay(Constants.SPLASH_INITIAL_DELAY)

        for (i in 1..tagline1.length) {
            tagline1Text = tagline1.substring(0, i)
            delay(Constants.TYPING_SPEED_MS)
        }

        delay(Constants.SPLASH_INTER_LINE_DELAY)

        for (i in 1..tagline2.length) {
            tagline2Text = tagline2.substring(0, i)
            delay(Constants.TYPING_SPEED_MS)
        }

        onAnimationComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SplashLogo(
                scale = logoScale,
                alpha = logoAlpha
            )

            Spacer(modifier = Modifier.height(Constants.SPLASH_SPACER_HEIGHT_DP.dp))

            SplashTagline(
                dividerWidth = dividerWidth,
                tagline1Text = tagline1Text,
                tagline2Text = tagline2Text
            )
        }
    }
}
