package com.iti.presentation.screens.ai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun AiChatShimmer(modifier: Modifier = Modifier) {
    val shimmerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .shimmer()
    ) {
        // AI bubble (left)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(shimmerColor, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(40.dp)
                    .background(shimmerColor, shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp))
            )
        }

        // User bubble (right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(40.dp)
                    .background(shimmerColor, shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp))
            )
        }

        // AI bubble with product cards shimmer (left)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(shimmerColor, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .height(60.dp)
                        .background(shimmerColor, shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp))
                )
                // Product card placeholder
                Box(
                    modifier = Modifier
                        .width(240.dp)
                        .height(100.dp)
                        .background(shimmerColor, shape = RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

@Composable
fun AiMessageShimmer(
    modifier: Modifier = Modifier
) {
    val shimmerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shimmer(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(shimmerColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(40.dp)
                .background(shimmerColor, shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp))
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun AiChatShimmerPreview() {
    MaterialTheme {
        AiChatShimmer()
    }
}
