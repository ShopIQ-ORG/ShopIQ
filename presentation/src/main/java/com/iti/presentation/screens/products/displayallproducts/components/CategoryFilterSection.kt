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
fun CategoryFilterSection(
    selectedCategories: Set<String>,
    categories: List<String>,
    onCategoryToggled: (String) -> Unit,
    onClearCategories: () -> Unit,
    onSelectAllCategories: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val initialLimit = 7
    val visibleCategories = if (isExpanded || categories.size <= initialLimit) {
        categories
    } else {
        categories.take(initialLimit)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        FilterSectionHeader(
            title = stringResource(R.string.filter_by_category),
            onSelectAllClick = onSelectAllCategories
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "All" chip
            val isAllSelected = selectedCategories.isEmpty()
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isAllSelected) Color.Black else Color.White
                    )
                    .border(
                        width = 1.dp,
                        color = if (isAllSelected) Color.Transparent else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onClearCategories() }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.all_subcategory),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isAllSelected) Color.White else Color.Black
                )
            }

            // Category chips
            visibleCategories.forEach { category ->
                val isSelected = selectedCategories.contains(category)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) Color.Black else Color.White
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.Transparent else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onCategoryToggled(category) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.uppercase(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) Color.White else Color.Black
                    )
                }
            }

            // Show more button
            if (categories.size > initialLimit) {
                val remaining = categories.size - initialLimit
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
