package com.iti.presentation.screens.productdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

private val ILLUSTRATION_HEIGHT = 340.dp
private val THUMBNAIL_SIZE = 72.dp
private val CIRCLE_SIZE = 36.dp
private val SQUARE_WIDTH = 54.dp
private val SQUARE_HEIGHT = 44.dp
private val SPACER_LARGE = 24.dp
private val SPACER_MEDIUM = 16.dp
private val SPACER_SMALL = 8.dp
private val CORNER_RADIUS_LARGE = 16.dp
private val CORNER_RADIUS_MEDIUM = 12.dp
private val CORNER_RADIUS_SMALL = 8.dp

@Composable
fun ProductDetailsShimmer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .shimmer()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(SPACER_MEDIUM)
    ) {
        // Image Gallery Shimmer Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ILLUSTRATION_HEIGHT),
            horizontalArrangement = Arrangement.spacedBy(SPACER_MEDIUM)
        ) {
            // Thumbnails column
            Column(
                modifier = Modifier
                    .width(THUMBNAIL_SIZE)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(SPACER_SMALL)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(THUMBNAIL_SIZE)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(CORNER_RADIUS_MEDIUM)
                            )
                    )
                }
            }

            // Main Image box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(CORNER_RADIUS_LARGE)
                    )
            )
        }

        Spacer(modifier = Modifier.height(SPACER_SMALL))

        // Title Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(28.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(CORNER_RADIUS_SMALL)
                )
        )

        // Price & Rating Shimmer Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(CORNER_RADIUS_SMALL)
                    )
            )

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(20.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(CORNER_RADIUS_SMALL)
                    )
            )
        }

        Spacer(modifier = Modifier.height(SPACER_SMALL))

        // Description Shimmer
        Column(verticalArrangement = Arrangement.spacedBy(SPACER_SMALL)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(CORNER_RADIUS_SMALL)
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(CORNER_RADIUS_SMALL)
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(CORNER_RADIUS_SMALL)
                    )
            )
        }

        Spacer(modifier = Modifier.height(SPACER_SMALL))

        // Colors Section Shimmer
        Column(verticalArrangement = Arrangement.spacedBy(SPACER_SMALL)) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(CORNER_RADIUS_SMALL)
                    )
            )
            Row(horizontalArrangement = Arrangement.spacedBy(SPACER_MEDIUM)) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(CIRCLE_SIZE)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(SPACER_SMALL))

        // Sizes Section Shimmer
        Column(verticalArrangement = Arrangement.spacedBy(SPACER_SMALL)) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(CORNER_RADIUS_SMALL)
                    )
            )
            Row(horizontalArrangement = Arrangement.spacedBy(SPACER_SMALL)) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .size(width = SQUARE_WIDTH, height = SQUARE_HEIGHT)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(CORNER_RADIUS_SMALL)
                            )
                    )
                }
            }
        }
    }
}
