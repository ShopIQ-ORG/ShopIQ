package com.iti.presentation.screens.address.components

import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.ui.theme.SuccessDark
import com.iti.presentation.ui.theme.SuccessLight
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.domain.models.Address
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.ui.theme.ShopIQTheme

@Composable
fun AddressLocationDetected(
    address: Address,
    onConfirmClick: (name: String, isDefault: Boolean) -> Unit,
    onEditLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTag by remember { mutableStateOf("Home") }
    var customTag by remember { mutableStateOf("") }
    var isDefaultAddress by remember { mutableStateOf(false) }

    val isDark = LocalDarkTheme.current
    val successColor = if (isDark) SuccessDark else SuccessLight

    val homeText = stringResource(R.string.address_tag_home)
    val workText = stringResource(R.string.address_tag_work)
    val otherText = stringResource(R.string.address_tag_other)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Map Preview Snapshot Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
        ) {
            OsmMapView(
                latitude = address.latitude,
                longitude = address.longitude,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Change location trigger button
        TextButton(
            onClick = onEditLocationClick,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.Default.EditLocation,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.address_btn_select_on_map),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Address Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = successColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.address_detected_label),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = successColor
                    )
                }

                Text(
                    text = address.street.ifBlank { stringResource(R.string.address_location_detected_title) },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (address.city.isNotEmpty() || address.postalCode.isNotEmpty()) {
                    Text(
                        text = "${address.city} ${address.postalCode}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = address.country,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tags & Details Form
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.address_save_as_label),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TagChip(
                    label = homeText,
                    isSelected = selectedTag == "Home",
                    onClick = { selectedTag = "Home" }
                )
                TagChip(
                    label = workText,
                    isSelected = selectedTag == "Work",
                    onClick = { selectedTag = "Work" }
                )
                TagChip(
                    label = otherText,
                    isSelected = selectedTag == "Other",
                    onClick = { selectedTag = "Other" }
                )
            }

            if (selectedTag == "Other") {
                OutlinedTextField(
                    value = customTag,
                    onValueChange = { customTag = it },
                    label = { Text(stringResource(R.string.address_tag_custom_placeholder)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Set default switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.address_set_default_label),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.address_set_default_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = isDefaultAddress,
                    onCheckedChange = { isDefaultAddress = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = successColor,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirmation Actions
        ShopIQButton(
            text = stringResource(R.string.address_btn_confirm),
            onClick = {
                val finalTagName = if (selectedTag == "Other") {
                    customTag.ifBlank { otherText }
                } else {
                    when (selectedTag) {
                        "Home" -> homeText
                        "Work" -> workText
                        else -> otherText
                    }
                }
                onConfirmClick(finalTagName, isDefaultAddress)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Light Mode")
@Composable
private fun AddressLocationDetectedLightPreview() {
    ShopIQTheme(darkTheme = false) {
        AddressLocationDetected(
            address = Address(
                id = "1",
                name = "Home",
                street = "9 Athar An Nabi Street",
                city = "Cairo",
                postalCode = "11511",
                country = "Egypt",
                latitude = 30.0054,
                longitude = 31.2332
            ),
            onConfirmClick = { _, _ -> },
            onEditLocationClick = {}
        )
    }
}

@Preview(name = "Dark Mode")
@Composable
private fun AddressLocationDetectedDarkPreview() {
    ShopIQTheme(darkTheme = true) {
        AddressLocationDetected(
            address = Address(
                id = "1",
                name = "Home",
                street = "9 Athar An Nabi Street",
                city = "Cairo",
                postalCode = "11511",
                country = "Egypt",
                latitude = 30.0054,
                longitude = 31.2332
            ),
            onConfirmClick = { _, _ -> },
            onEditLocationClick = {}
        )
    }
}
