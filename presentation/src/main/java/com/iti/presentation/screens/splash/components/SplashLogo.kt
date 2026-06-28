package com.iti.presentation.screens.splash.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.util.Constants

@Composable
fun SplashLogo(
    scale: Float,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    val isDark = LocalDarkTheme.current
    val logoResId = if (isDark) R.drawable.logo_dark else R.drawable.logo_light

    Image(
        painter = painterResource(id = logoResId),
        contentDescription = "ShopIQ Logo",
        modifier = modifier
            .size(Constants.SPLASH_LOGO_SIZE_DP.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
    )
}
