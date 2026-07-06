package com.iti.presentation.screens.ai.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VoiceWaveformVisualizer(
    isDark: Boolean
) {
    val bubbleColor = if (isDark) Color(0xFF3B1E78) else Color(0xFFE8DDFF)
    
    Box(
        modifier = Modifier
            .background(bubbleColor, shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .width(200.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice search",
                tint = Color(0xFF6F32E5),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val heights = listOf(12, 18, 14, 28, 8, 22, 16, 26, 12, 20, 10, 18)
                heights.forEachIndexed { index, h ->
                    val infiniteTransition = rememberInfiniteTransition(label = "waveform_$index")
                    val animatedHeightMultiplier by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(400 + (index * 50) % 300, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "height_$index"
                    )
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height((h * animatedHeightMultiplier).dp)
                            .background(Color(0xFF6F32E5), shape = RoundedCornerShape(1.dp))
                    )
                }
            }
        }
    }
}
