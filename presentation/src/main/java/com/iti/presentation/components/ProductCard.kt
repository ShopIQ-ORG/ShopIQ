package com.iti.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.domain.models.Product
import com.iti.presentation.R
import com.iti.presentation.ui.theme.WarningLight
import com.iti.presentation.util.CurrencyManager
import com.iti.presentation.util.ReviewsCache
import com.iti.presentation.util.compareAtPrice
import com.iti.presentation.util.discountPercent
import com.iti.presentation.util.getLocalizedCode


@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    var showRemoveConfirmation by remember { mutableStateOf(false) }

    val cardBackground = MaterialTheme.colorScheme.surfaceVariant
    val onCardPrimaryText = MaterialTheme.colorScheme.onSurface
    val onCardSecondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val discountColor = MaterialTheme.colorScheme.error

    // Observe ReviewsCache: shows fresh reviews immediately after a review is submitted
    val reviewsCacheMap by ReviewsCache.cache.collectAsState()
    val cleanProductId = product.id.substringAfterLast("/")
    val effectiveReviews = reviewsCacheMap[cleanProductId] ?: product.reviews

    Column(
        modifier = modifier
            .width(172.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.42f),
                spotColor = Color.Black.copy(alpha = 0.55f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(cardBackground)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            CustomNetworkImage(
                imageUrl = product.images.firstOrNull()?.url.orEmpty(),
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.30f))
                        )
                    )
            )

            val discountPercent = product.discountPercent
            if (discountPercent > 0) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(discountColor, discountColor.copy(alpha = 0.82f))
                            )
                        )
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.product_discount_badge, discountPercent),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(34.dp)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.92f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (product.isFavorite) {
                            showRemoveConfirmation = true
                        } else {
                            onFavoriteClick()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(id = R.string.content_desc_favorite),
                    tint = if (product.isFavorite) discountColor else Color(0xFF263238),
                    modifier = Modifier.size(18.dp)
                )
            }

            val totalReviews = effectiveReviews.size
            val averageRating = if (totalReviews > 0) effectiveReviews.map { it.rating }.average() else 0.0

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = stringResource(id = R.string.content_desc_rating),
                    tint = WarningLight,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = if (totalReviews > 0) {
                        String.format(java.util.Locale.US, "%.1f (%d)", averageRating, totalReviews)
                    } else {
                        "0.0"
                    },
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val isArabic = com.iti.presentation.util.LocaleHelper.isArabic()
            Text(
                text = if (isArabic) (product.arTitle ?: product.title) else product.title,
                style = MaterialTheme.typography.bodyMedium,
                color = onCardPrimaryText,
                fontWeight = FontWeight.SemiBold,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                val currentCurrency by CurrencyManager.selectedCurrency.collectAsState()
                val convertedMinPrice = CurrencyManager.convertFromUsd(
                    product.minPrice.amount.toDoubleOrNull() ?: 0.0
                )
                val minPriceStr = if (convertedMinPrice % 1.0 == 0.0) {
                    "%.0f".format(convertedMinPrice)
                } else {
                    "%.2f".format(convertedMinPrice)
                }
                val currencyLabel = currentCurrency.getLocalizedCode(LocalContext.current)

                val convertedCompareAt = product.compareAtPrice?.amount?.toDoubleOrNull()
                    ?.let { CurrencyManager.convertFromUsd(it) }
                val hasDiscount = convertedCompareAt != null && convertedCompareAt > convertedMinPrice

                Text(
                    text = "$minPriceStr $currencyLabel",
                    style = MaterialTheme.typography.labelLarge,
                    color = onCardPrimaryText,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (hasDiscount) {
                    val compareStr = if (convertedCompareAt % 1.0 == 0.0) {
                        "%.0f".format(convertedCompareAt)
                    } else {
                        "%.2f".format(convertedCompareAt)
                    }
                    Text(
                        text = "$compareStr $currencyLabel",
                        style = MaterialTheme.typography.labelSmall,
                        color = onCardSecondaryText,
                        textDecoration = TextDecoration.LineThrough,
                        fontSize = 11.sp
                    )
                } else {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }

    if (showRemoveConfirmation) {
        ConfirmationDialog(
            title = stringResource(id = R.string.remove_favorite_title),
            message = stringResource(id = R.string.remove_favorite_message),
            confirmText = stringResource(id = R.string.remove_favorite_confirm),
            dismissText = stringResource(id = R.string.remove_favorite_cancel),
            onConfirm = {
                showRemoveConfirmation = false
                onFavoriteClick()
            },
            onDismiss = { showRemoveConfirmation = false }
        )
    }
}