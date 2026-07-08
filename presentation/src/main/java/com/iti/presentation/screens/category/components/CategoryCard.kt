package com.iti.presentation.screens.category.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iti.presentation.R
import com.iti.presentation.screens.category.model.CategoryItem
import com.iti.presentation.ui.theme.ShopIQTheme
import kotlin.math.absoluteValue

private val CategoryColorPairs = listOf(
    Color(0xFF1E3A8A) to Color(0xFF0D9488), // Deep Navy to Teal
    Color(0xFF374151) to Color(0xFFE11D48), // Charcoal to Rose
    Color(0xFF065F46) to Color(0xFF65A30D), // Forest to Lime
    Color(0xFF5B21B6) to Color(0xFF9333EA), // Violet to Purple
    Color(0xFF9D174D) to Color(0xFFF59E0B), // Burgundy to Amber
    Color(0xFF1E40AF) to Color(0xFF3B82F6), // Dark Blue to Blue
    Color(0xFF0F766E) to Color(0xFF10B981), // Dark Teal to Emerald
    Color(0xFF7F1D1D) to Color(0xFFDC2626)  // Dark Red to Red
)

@Composable
fun getCategoryGradient(title: String): List<Color> {
    val index = title.hashCode().absoluteValue % CategoryColorPairs.size
    val pair = CategoryColorPairs[index]
    return listOf(pair.first, pair.second)
}

@Composable
fun getCategoryMonogram(title: String): String {
    val words = title.trim().split(Regex("\\s+"))
    return when {
        words.isEmpty() -> ""
        words.size == 1 -> words[0].take(2).uppercase()
        else -> "${words[0].first()}${words[1].first()}".uppercase()
    }
}

@Composable
fun CategoryCardFallback(title: String, modifier: Modifier = Modifier) {
    val gradientColors = getCategoryGradient(title)
    val monogram = getCategoryMonogram(title)
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(300f, 300f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = monogram,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = Color.White.copy(alpha = 0.5f),
            letterSpacing = 4.sp
        )
    }
}

@Composable
fun CategoryCard(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (category.imageAssetPath.isEmpty()) {
                CategoryCardFallback(title = category.title)
            } else {
                var isImageError by remember { mutableStateOf(false) }
                
                if (isImageError) {
                    CategoryCardFallback(title = category.title)
                } else {
                    AsyncImage(
                        model = category.imageAssetPath,
                        contentDescription = category.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        onState = { state ->
                            if (state is coil.compose.AsyncImagePainter.State.Error) {
                                isImageError = true
                            }
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.15f),
                                Color.Black.copy(alpha = 0.72f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            val isArabic = com.iti.presentation.util.LocaleHelper.isArabic()
            val displayName = if (isArabic) (category.arTitle ?: category.title) else category.title
            Text(
                text = displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                letterSpacing = 0.2.sp,
                maxLines = 2,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "With image", widthDp = 180)
@Composable
private fun CategoryCardWithImagePreview() {
    ShopIQTheme {
        CategoryCard(
            category = CategoryItem(
                id = "1",
                title = "Snowboards",
                imageAssetPath = "https://picsum.photos/200/300"
            ),
            onClick = {},
            modifier = Modifier.size(180.dp, 210.dp)
        )
    }
}

@Preview(showBackground = true, name = "No image", widthDp = 180)
@Composable
private fun CategoryCardNoImagePreview() {
    ShopIQTheme {
        CategoryCard(
            category = CategoryItem(
                id = "2",
                title = "Accessories",
                imageAssetPath = ""
            ),
            onClick = {},
            modifier = Modifier.size(180.dp, 210.dp)
        )
    }
}