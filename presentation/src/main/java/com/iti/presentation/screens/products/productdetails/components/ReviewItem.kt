package com.iti.presentation.screens.products.productdetails.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.ripple.rememberRipple
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
    val initialHelpfulCount = remember(review.id) { if (review.id.startsWith("mock")) (3..24).random() else 0 }
    var helpfulCount by remember { mutableStateOf(initialHelpfulCount) }
    var isHelpfulClicked by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            ReviewHeader(
                review = review,
                isOwnReview = isOwnReview,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (!review.title.isNullOrBlank()) {
                Text(
                    text = review.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = review.body,
                fontSize = 13.5.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun ReviewHeader(
    review: ProductReview,
    isOwnReview: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReviewAvatar(avatarUrl = review.avatarUrl, customerName = review.customerName)

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = review.customerName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StarRatingRow(rating = review.rating, starSize = 13.dp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "•",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatReviewDate(review.createdAt),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        if (isOwnReview) {
            ReviewOptionsMenu(onEditClick = onEditClick, onDeleteClick = onDeleteClick)
        }
    }
}

@Composable
private fun ReviewAvatar(avatarUrl: String?, customerName: String) {
    var isImageError by remember { mutableStateOf(false) }
    val showInitials = avatarUrl.isNullOrBlank() || isImageError

    if (!showInitials) {
        coil.compose.AsyncImage(
            model = avatarUrl,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            onError = { isImageError = true }
        )
    } else {
        val initials = if (customerName.isNotBlank()) customerName.trim().first().uppercase() else "A"
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun ReviewOptionsMenu(onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    Box {
        IconButton(
            onClick = { showMenu = true },
            modifier = Modifier.size(26.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Options",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
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
}