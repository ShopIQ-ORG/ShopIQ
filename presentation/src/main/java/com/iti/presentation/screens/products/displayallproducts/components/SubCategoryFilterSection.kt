package com.iti.presentation.screens.products.displayallproducts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.presentation.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubCategoryFilterSection(
    selectedSubCategories: Set<String>,
    subCategories: List<String>,
    onSubCategoryToggled: (String) -> Unit,
    onClearSubCategories: () -> Unit,
    onSelectAllSubCategories: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val initialLimit = 7
    val visibleSubCategories = if (isExpanded || subCategories.size <= initialLimit) {
        subCategories
    } else {
        subCategories.take(initialLimit)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        FilterSectionHeader(
            title = stringResource(R.string.filter_by_subcategory),
            onSelectAllClick = onSelectAllSubCategories
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "All" chip
            val isAllSelected = selectedSubCategories.isEmpty()
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        width = 1.dp,
                        color = if (isAllSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onClearSubCategories() }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.all_subcategory),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isAllSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Subcategory chips
            visibleSubCategories.forEach { subCategory ->
                val isSelected = selectedSubCategories.contains(subCategory)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onSubCategoryToggled(subCategory) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = subCategory,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Show more button
            if (subCategories.size > initialLimit) {
                val remaining = subCategories.size - initialLimit
                Text(
                    text = if (isExpanded) stringResource(R.string.filter_show_less) else stringResource(R.string.filter_show_more, remaining),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 8.dp, vertical = 10.dp) // padding BEFORE clickable increases target size!
                        .clickable { isExpanded = !isExpanded }
                )
            }
        }
    }
}
