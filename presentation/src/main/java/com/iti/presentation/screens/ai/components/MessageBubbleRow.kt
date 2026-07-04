package com.iti.presentation.screens.ai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinilk.shimmer.shimmer
import com.iti.presentation.R
import com.iti.presentation.components.CustomNetworkImage
import com.iti.presentation.screens.ai.AiChatContract.ChatMessageUi

@Composable
fun MessageBubbleRow(
    message: ChatMessageUi,
    isDark: Boolean,
    onProductClick: (Long) -> Unit
) {
    val isUser = message.sender == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
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
                Icon(
                    painter = painterResource(id = R.drawable.ic_ai),
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            if (!message.attachedImageUrl.isNullOrBlank()) {
                UploadedImagePreview(imageUrl = message.attachedImageUrl)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (message.text.isNotBlank()) {
                TextBubble(text = message.text, isUser = isUser, isDark = isDark)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (message.isResolvingProducts) {
                ProductCardShimmer(isDark = isDark)
                Spacer(modifier = Modifier.height(8.dp))
            } else if (message.recommendedProducts.isNotEmpty()) {
                if (message.recommendedProducts.size == 1) {
                    SingleProductCard(
                        product = message.recommendedProducts.first(),
                        isDark = isDark,
                        onProductClick = onProductClick
                    )
                } else {
                    ProductSuggestionsCard(
                        products = message.recommendedProducts,
                        isDark = isDark,
                        onProductClick = onProductClick
                    )
                }
            }
        }
    }
}

@Composable
fun TextBubble(
    text: String,
    isUser: Boolean,
    isDark: Boolean
) {
    val bubbleColor = if (isUser) {
        if (isDark) Color(0xFF3B1E78) else Color(0xFFE8DDFF)
    } else {
        if (isDark) Color(0xFF242A31) else Color(0xFFF3F4F6)
    }
    
    val textColor = if (isUser) {
        if (isDark) Color(0xFFE5D5FF) else Color(0xFF4A148C)
    } else {
        MaterialTheme.colorScheme.onBackground
    }
    
    val shape = if (isUser) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }
    
    val displayText = when (text) {
        "ERROR_INVALID_KEY" -> stringResource(id = R.string.ai_error_invalid_key)
        "ERROR_QUOTA" -> stringResource(id = R.string.ai_error_quota_exceeded)
        "ERROR_NETWORK" -> stringResource(id = R.string.ai_error_network)
        "ERROR_IMAGE" -> stringResource(id = R.string.ai_error_image_processing)
        "ERROR_UNKNOWN" -> stringResource(id = R.string.ai_error_unknown)
        else -> text
    }

    Box(
        modifier = Modifier
            .background(bubbleColor, shape = shape)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = displayText,
            fontSize = 14.sp,
            color = textColor,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun UploadedImagePreview(
    imageUrl: String
) {
    Card(
        modifier = Modifier
            .size(160.dp)
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        CustomNetworkImage(
            imageUrl = imageUrl,
            contentDescription = "Uploaded Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ProductCardShimmer(
    isDark: Boolean
) {
    val shimmerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .shimmer(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1E242B) else Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(shimmerColor, shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(14.dp)
                        .background(shimmerColor, shape = RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(10.dp)
                        .background(shimmerColor, shape = RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun MessageBubbleRowUserPreview() {
    MaterialTheme {
        MessageBubbleRow(
            message = ChatMessageUi(
                id = "1",
                sender = "user",
                text = "Hello Eslam, show me some Adidas shoes!",
                timestamp = System.currentTimeMillis()
            ),
            isDark = false,
            onProductClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun MessageBubbleRowAiPreview() {
    MaterialTheme {
        MessageBubbleRow(
            message = ChatMessageUi(
                id = "2",
                sender = "ai",
                text = "Sure! Here is a classic Adidas backpack that you might love.",
                timestamp = System.currentTimeMillis()
            ),
            isDark = false,
            onProductClick = {}
        )
    }
}
