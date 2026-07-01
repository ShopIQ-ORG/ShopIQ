package com.iti.presentation.screens.products.displayallproducts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.screens.products.displayallproducts.AllProductsContract.FilterState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filterState: FilterState,
    availableCategories: List<String>,
    availableSubCategories: List<String>,
    availableBrands: List<String>,
    onCategorySelected: (String?) -> Unit,
    onSubCategorySelected: (String?) -> Unit,
    onBrandToggled: (String) -> Unit,
    onBrandSearchChanged: (String) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // ── Header ────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.filter_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onReset) {
                    Text(
                        text = stringResource(R.string.filter_reset),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
            ) {
                // ── Filter by Category ─────────────────────────────────────────
                item {
                    FilterSectionHeader(title = stringResource(R.string.filter_by_category))
                    DropdownSelector(
                        selected = filterState.selectedCategory,
                        placeholder = stringResource(R.string.filter_select_category),
                        options = availableCategories,
                        onSelected = onCategorySelected,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
                }

                // ── Filter by Sub-Category ─────────────────────────────────────
                item {
                    FilterSectionHeader(title = stringResource(R.string.filter_by_subcategory))
                    DropdownSelector(
                        selected = filterState.selectedSubCategory,
                        placeholder = stringResource(R.string.filter_select_subcategory),
                        options = availableSubCategories,
                        onSelected = onSubCategorySelected,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
                }

                // ── Filter by Brand header ────────────────────────────────────
                item {
                    FilterSectionHeader(title = stringResource(R.string.filter_by_brand))
                    // brand search field
                    TextField(
                        value = filterState.brandSearchQuery,
                        onValueChange = onBrandSearchChanged,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.filter_search_brand),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                }

                // ── Brand checkboxes ──────────────────────────────────────────
                val filteredBrands = if (filterState.brandSearchQuery.isBlank()) availableBrands
                else availableBrands.filter { it.contains(filterState.brandSearchQuery, ignoreCase = true) }

                items(filteredBrands) { brand ->
                    BrandCheckboxRow(
                        brand = brand,
                        checked = brand in filterState.selectedBrands,
                        onToggle = { onBrandToggled(brand) }
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            // ── Apply button ───────────────────────────────────────────────────
            Button(
                onClick = onApply,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text(
                    text = stringResource(R.string.filter_apply),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun FilterSectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = stringResource(R.string.filter_cd_expand),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DropdownSelector(
    selected: String?,
    placeholder: String,
    options: List<String>,
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Trigger row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { expanded = !expanded }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selected ?: placeholder,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected != null) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                    )
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // "None" option to clear
                if (selected != null) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onSelected(null)
                                expanded = false
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                    HorizontalDivider()
                }
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onSelected(option)
                                expanded = false
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (option == selected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                }
            }
        }
    }
}

@Composable
private fun BrandCheckboxRow(
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
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.onBackground,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = brand,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
