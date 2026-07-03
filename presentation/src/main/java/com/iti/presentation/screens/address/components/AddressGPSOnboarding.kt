package com.iti.presentation.screens.address.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.ui.theme.SuccessDark
import com.iti.presentation.ui.theme.SuccessLight

@Composable
fun AddressGPSOnboarding(
    onUseGPSClick: () -> Unit,
    isDetecting: Boolean,
    modifier: Modifier = Modifier
) {
    // Pulse animation for radar effect
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale1"
    )
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha1"
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale2"
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha2"
    )

    val isDark = LocalDarkTheme.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val successColor = if (isDark) SuccessDark else SuccessLight

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Center Animated Radar Graphic
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Outer Pulse Ring 1
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(scale1)
                        .graphicsLayer { alpha = alpha1 }
                        .border(2.dp, primaryColor, CircleShape)
                )

                // Outer Pulse Ring 2
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(scale2)
                        .graphicsLayer { alpha = alpha2 }
                        .border(1.5.dp, primaryColor.copy(alpha = 0.6f), CircleShape)
                )

                // Main circular container
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier
                            .size(48.dp)
                            .graphicsLayer { rotationZ = 45f }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = if (isDetecting) stringResource(R.string.address_gps_detecting) else stringResource(R.string.address_gps_search),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.address_gps_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Checkmarks
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = successColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.address_gps_feature_accuracy),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = successColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.address_gps_feature_secure),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Action CTA
        ShopIQButton(
            text = if (isDetecting) stringResource(R.string.address_gps_searching_btn) else stringResource(R.string.address_gps_use_btn),
            onClick = onUseGPSClick,
            leadingIcon = Icons.Default.MyLocation,
            isLoading = isDetecting,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Light Mode")
@Composable
private fun AddressGPSOnboardingLightPreview() {
    ShopIQTheme(darkTheme = false) {
        AddressGPSOnboarding(
            onUseGPSClick = {},
            isDetecting = false
        )
    }
}

@Preview(name = "Dark Mode - Detecting")
@Composable
private fun AddressGPSOnboardingDetectingDarkPreview() {
    ShopIQTheme(darkTheme = true) {
        AddressGPSOnboarding(
            onUseGPSClick = {},
            isDetecting = true
        )
    }
}
