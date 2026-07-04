package com.iti.presentation.screens.ai.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.iti.presentation.R

@Composable
fun AiTypingIndicator(
    isDark: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(end = 8.dp, top = 4.dp)
                .size(28.dp)
                .background(
                    brush = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF4F46E5))),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ai_assistent),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Box(
            modifier = Modifier
                .background(
                    if (isDark) Color(0xFF242A31) else Color(0xFFF3F4F6),
                    shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val transition = rememberInfiniteTransition(label = "dots")
                
                val dot1Alpha by transition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot1"
                )
                
                val dot2Alpha by transition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = 200, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot2"
                )
                
                val dot3Alpha by transition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = 400, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot3"
                )

                val dotColor = if (isDark) Color(0xFF9CA3AF) else Color(0xFF4B5563)

                Box(modifier = Modifier.size(6.dp).background(dotColor.copy(alpha = dot1Alpha), CircleShape))
                Box(modifier = Modifier.size(6.dp).background(dotColor.copy(alpha = dot2Alpha), CircleShape))
                Box(modifier = Modifier.size(6.dp).background(dotColor.copy(alpha = dot3Alpha), CircleShape))
            }
        }
    }
}
