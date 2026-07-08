package com.iti.presentation.screens.products.productdetails.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.domain.models.ProductReview
import com.iti.presentation.R

@Composable
fun ReviewItem(
    review: ProductReview,
    isOwnReview: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initialHelpfulCount = remember(review.id) { if (review.id.startsWith("mock")) (1..15).random() else 0 }
    var helpfulCount by remember { mutableStateOf(initialHelpfulCount) }
    var isHelpfulClicked by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: Avatar + Name + Date + Verified Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var isImageError by remember { mutableStateOf(false) }
                val showInitials = review.avatarUrl.isNullOrBlank() || isImageError

                if (!showInitials) {
                    coil.compose.AsyncImage(
                        model = review.avatarUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        onError = { isImageError = true }
                    )
                } else {
                    val initials = if (review.customerName.isNotBlank()) {
                        review.customerName.trim().first().uppercase()
                    } else "A"
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & Date
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.customerName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatReviewDate(review.createdAt),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                    )
                }


            }

            Spacer(modifier = Modifier.height(10.dp))

            // Star Rating
            StarRatingRow(rating = review.rating, starSize = 16.dp)

            Spacer(modifier = Modifier.height(8.dp))

            // Review Body Text
            Text(
                text = review.body,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                textAlign = TextAlign.Start
            )

            // Bottom Actions Row: Vertical dots + Helpful thumbs up
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vertical dots menu (only shown for the owner of the review)
                if (isOwnReview) {
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.edit_review_label), fontSize = 14.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onEditClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.delete_review_label), color = MaterialTheme.colorScheme.error, fontSize = 14.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.size(32.dp))
                }

                // Helpful Action
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (!isHelpfulClicked) {
                            helpfulCount++
                            isHelpfulClicked = true
                        } else {
                            helpfulCount--
                            isHelpfulClicked = false
                        }
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.was_review_helpful),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Helpful",
                        tint = if (isHelpfulClicked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = helpfulCount.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isHelpfulClicked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
