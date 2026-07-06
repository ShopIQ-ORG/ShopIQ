package com.iti.presentation.screens.products.displayallproducts.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.presentation.R

@Composable
fun BrandFilterSection(
    selectedBrands: Set<String>,
    availableBrands: List<String>,
    onBrandToggled: (String) -> Unit,
    onSelectAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val initialLimit = 6 // Show first 6 brands initially (3 rows of 2 columns)

    val visibleBrands = if (isExpanded || availableBrands.size <= initialLimit) {
        availableBrands
    } else {
        availableBrands.take(initialLimit)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        FilterSectionHeader(
            title = stringResource(R.string.filter_by_brand),
            onSelectAllClick = onSelectAll
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 2-column brand checkboxes
        val chunked = visibleBrands.chunked(2)
        chunked.forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Item 1
                Box(modifier = Modifier.weight(1f)) {
                    BrandCheckboxItem(
                        brand = pair[0],
                        checked = selectedBrands.contains(pair[0]),
                        onToggle = { onBrandToggled(pair[0]) }
                    )
                }
                // Item 2
                Box(modifier = Modifier.weight(1f)) {
                    if (pair.size > 1) {
                        BrandCheckboxItem(
                            brand = pair[1],
                            checked = selectedBrands.contains(pair[1]),
                            onToggle = { onBrandToggled(pair[1]) }
                        )
                    } else {
                        Spacer(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }

        // Show more button
        if (availableBrands.size > initialLimit) {
            val remaining = availableBrands.size - initialLimit
            Text(
                text = if (isExpanded) stringResource(R.string.filter_show_less) else stringResource(R.string.filter_show_more, remaining),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .padding(vertical = 12.dp) // padding BEFORE clickable increases touch target area!
                    .clickable { isExpanded = !isExpanded }
            )
        }
    }
}

@Composable
private fun BrandCheckboxItem(
    brand: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggle
            )
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null, // Set null to delegate click handling completely to the parent Row
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                checkmarkColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = brand.uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
