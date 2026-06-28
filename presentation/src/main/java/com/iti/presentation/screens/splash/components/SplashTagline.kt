package com.iti.presentation.screens.splash.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.util.Constants

@Composable
fun SplashTagline(
    dividerWidth: Dp,
    tagline1Text: String,
    tagline2Text: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .width(dividerWidth)
                .height(Constants.SPLASH_DIVIDER_HEIGHT_DP.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        Spacer(modifier = Modifier.height(Constants.SPLASH_DIVIDER_SPACER_DP.dp))

        Text(
            text = tagline1Text,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = Constants.SPLASH_LETTER_SPACING_SP.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Constants.SPLASH_LINE_SPACER_DP.dp))

        Text(
            text = tagline2Text,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = Constants.SPLASH_LETTER_SPACING_SP.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}
