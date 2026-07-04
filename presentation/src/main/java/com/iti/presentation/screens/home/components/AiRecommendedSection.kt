package com.iti.presentation.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinilk.shimmer.shimmer
import com.iti.domain.models.Product
import com.iti.presentation.R
import com.iti.presentation.components.ProductCard

/**
 * Displays an AI-powered "Recommended For You" section on the Home screen.
 *
 * - If [products] is non-empty: shows a horizontal LazyRow of product cards.
 * - If [isLoading]: shows shimmer placeholders.
 * - If empty and not loading: shows a "Try Eslam" promo card.
 */
@Composable
fun AiRecommendedSection(
    products: List<Product>,
    isLoading: Boolean,
    onProductClick: (Product) -> Unit,
    onTryEslamClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // ── Section Header ──────────────────────────────────────────────────
        AiSectionHeader()

        // ── Content ─────────────────────────────────────────────────────────
        when {
            isLoading -> AiRecommendedShimmer()

            products.isNotEmpty() -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(products) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product) },
                            onFavoriteClick = {},
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
            }

            else -> TryEslamCard(onTryEslamClick = onTryEslamClick)
        }
    }
}

// ── Private helpers ──────────────────────────────────────────────────────────

@Composable
private fun AiSectionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gradient avatar matching the AI bot style
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF8B5CF6), Color(0xFF4F46E5))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ai_assistent),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = stringResource(R.string.recommended_for_you),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.ai_recommended_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TryEslamCard(onTryEslamClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF8B5CF6).copy(alpha = 0.15f), Color(0xFF4F46E5).copy(alpha = 0.10f))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.try_eslam_title),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.try_eslam_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onTryEslamClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6F32E5)
                        ),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.try_eslam_btn),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Large decorative AI avatar on the right
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF8B5CF6), Color(0xFF4F46E5))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ai_assistent),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AiRecommendedShimmer() {
    val shimmerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shimmer()
    ) {
        items(4) {
            Column(
                modifier = Modifier.width(160.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(shimmerColor)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp)
                        .background(shimmerColor, RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(12.dp)
                        .background(shimmerColor, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}
