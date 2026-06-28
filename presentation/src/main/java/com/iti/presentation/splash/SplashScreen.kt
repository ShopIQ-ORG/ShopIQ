package com.iti.presentation.splash

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R
import com.iti.presentation.util.Constants
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Logo pop-in animation (scale & alpha) using constants
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

    // Animated divider line width using constants
    val dividerWidth by animateDpAsState(
        targetValue = if (startAnimation) Constants.SPLASH_DIVIDER_WIDTH_TARGET.dp else 0.dp,
        animationSpec = tween(Constants.SPLASH_DIVIDER_DURATION, delayMillis = Constants.SPLASH_DIVIDER_DELAY),
        label = "dividerWidth"
    )

    // Typing effect for the tagline strings
    var tagline1Text by remember { mutableStateOf("") }
    var tagline2Text by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        startAnimation = true

        // Wait for the logo pop-in bounce to settle slightly
        delay(Constants.SPLASH_INITIAL_DELAY)

        // Type out the first line of the tagline
        val line1 = Constants.SPLASH_TAGLINE_1
        for (i in 1..line1.length) {
            tagline1Text = line1.substring(0, i)
            delay(Constants.TYPING_SPEED_MS)
        }

        delay(Constants.SPLASH_INTER_LINE_DELAY) // Small pause between lines

        // Type out the second line of the tagline
        val line2 = Constants.SPLASH_TAGLINE_2
        for (i in 1..line2.length) {
            tagline2Text = line2.substring(0, i)
            delay(Constants.TYPING_SPEED_MS)
        }
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
            // Larger Logo with smooth pop-in animation
            Image(
                painter = painterResource(id = R.drawable.logo_light),
                contentDescription = "ShopIQ Logo",
                modifier = Modifier
                    .size(220.dp)
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                        alpha = logoAlpha
                    }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tagline container with matching styling
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Expanding animated divider line
                Box(
                    modifier = Modifier
                        .width(dividerWidth)
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Line 1: Typing text
                Text(
                    text = tagline1Text,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Line 2: Typing text
                Text(
                    text = tagline2Text,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
