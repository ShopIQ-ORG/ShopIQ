package com.iti.presentation.screens.products.productdetails.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val EmptyStarColor = Color(0xFF9E9E9E)

@Composable
fun StarRatingRow(
    rating: Int,
    maxStars: Int = 5,
    starSize: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(maxStars) { index ->
            val isFilled = index < rating
            Icon(
                imageVector = if (isFilled) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (isFilled) Color(0xFFFFA726) else EmptyStarColor,
                modifier = Modifier.size(starSize)
            )
        }
    }
}

@Composable
fun DecimalStarRatingRow(
    rating: Double,
    modifier: Modifier = Modifier,
    starSize: Dp = 18.dp
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val starValue = index + 1
            val isFull = rating >= starValue
            val isHalf = !isFull && rating >= (starValue - 0.5)
            val isEmpty = !isFull && !isHalf

            Icon(
                imageVector = when {
                    isFull -> Icons.Filled.Star
                    isHalf -> Icons.AutoMirrored.Filled.StarHalf
                    else -> Icons.Outlined.Star
                },
                contentDescription = null,
                tint = if (isEmpty) EmptyStarColor else Color(0xFFFFA726),
                modifier = Modifier.size(starSize)
            )
        }
    }
}