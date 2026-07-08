package com.iti.presentation.screens.products.productdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R
import com.iti.presentation.ui.theme.ErrorDark
import com.iti.presentation.ui.theme.ErrorLight
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.ui.theme.WarningLight

@Composable
fun ProductInfoBlock(
    title: String,
    currencyCode: String,
    amount: String,
    description: String,
    rating: Double,
    reviewsCount: Int,
    compareAtAmount: String? = null,
    discountPercent: Int = 0
) {
    val isDark = isSystemInDarkTheme()
    val discountColor = if (isDark) ErrorDark else ErrorLight

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 24.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "$currencyCode $amount",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (!compareAtAmount.isNullOrBlank() || discountPercent > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (!compareAtAmount.isNullOrBlank()) {
                            Text(
                                text = "$currencyCode $compareAtAmount",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }

                        if (discountPercent > 0) {
                            Text(
                                text = "-$discountPercent%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(discountColor)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(text = "★", color = WarningLight, fontSize = 13.sp)
                Text(
                    text = String.format(java.util.Locale.US, "%.1f", rating),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "($reviewsCount ${stringResource(id = R.string.ratings_count)})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        var isExpanded by remember { mutableStateOf(false) }
        val trimmedDesc = description.trim()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = trimmedDesc.ifEmpty { stringResource(id = R.string.no_description) },
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 19.sp,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )
            if (trimmedDesc.length > 100) {
                Text(
                    text = if (isExpanded) stringResource(id = R.string.read_less) else stringResource(id = R.string.read_more),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Preview(name = "Product Info - Long Title", showBackground = true)
@Composable
private fun ProductInfoBlockLongTitlePreview() {
    ShopIQTheme {
        ProductInfoBlock(
            title = "Premium Heavyweight Oversized Cotton Fleece Hoodie With Kangaroo Pocket",
            currencyCode = "EGP",
            amount = "1,299.00",
            compareAtAmount = "1,599.00",
            discountPercent = 19,
            description = "A relaxed-fit hoodie made from heavyweight cotton fleece, " +
                    "featuring a kangaroo pocket and ribbed cuffs.",
            rating = 4.6,
            reviewsCount = 128
        )
    }
}