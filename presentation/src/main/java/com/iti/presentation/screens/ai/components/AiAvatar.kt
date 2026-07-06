package com.iti.presentation.screens.ai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.iti.presentation.R

@Composable
fun AiAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    iconSize: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                brush = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF4F46E5))),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = R.drawable.ai_assistent,
            contentDescription = "AI Assistant",
            modifier = Modifier.size(iconSize)
        )
    }
}
