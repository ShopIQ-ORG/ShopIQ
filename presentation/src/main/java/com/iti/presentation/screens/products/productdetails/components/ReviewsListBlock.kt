package com.iti.presentation.screens.products.productdetails.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.domain.models.ProductReview
import com.iti.presentation.R

@Composable
fun ReviewsListBlock(
    reviews: List<ProductReview>,
    currentUserName: String?,
    onWriteReviewClick: () -> Unit,
    onEditReviewClick: (ProductReview) -> Unit,
    onDeleteReviewClick: (ProductReview) -> Unit,
    modifier: Modifier = Modifier
) {
    val baseReviews = getReviewsOrDefault(reviews)
    val totalCount = baseReviews.size

    // Sort state: 0 = newest first, 1 = oldest first, 2 = highest rating
    var sortIndex by remember { mutableStateOf(0) }
    var showSortMenu by remember { mutableStateOf(false) }

    val sortLabels = listOf(
        stringResource(id = R.string.sort_latest),
        stringResource(id = R.string.sort_oldest),
        stringResource(id = R.string.sort_highest_rating)
    )

    val finalReviews = remember(baseReviews, sortIndex) {
        when (sortIndex) {
            1 -> baseReviews.sortedBy { it.createdAt }
            2 -> baseReviews.sortedByDescending { it.rating }
            else -> baseReviews.sortedByDescending { it.createdAt }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${stringResource(id = R.string.all_reviews)} ($totalCount)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sort Dropdown
                Box {
                    Row(
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .clickable { showSortMenu = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sortLabels[sortIndex],
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        sortLabels.forEachIndexed { index, label ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        color = if (index == sortIndex) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    sortIndex = index
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Write Review button
        OutlinedButton(
            onClick = onWriteReviewClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.write_review_btn),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Reviews list or empty state
        if (totalCount == 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Text(
                    text = stringResource(id = R.string.no_reviews_yet),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                finalReviews.forEach { review ->
                    val isOwnReview = currentUserName != null &&
                        review.customerName.trim().equals(currentUserName.trim(), ignoreCase = true)
                    ReviewItem(
                        review = review,
                        isOwnReview = isOwnReview,
                        onEditClick = { onEditReviewClick(review) },
                        onDeleteClick = { onDeleteReviewClick(review) }
                    )
                }
            }
        }
    }
}
