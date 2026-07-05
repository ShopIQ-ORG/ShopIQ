//
//  AddressItem.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.address.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.domain.models.Address
import com.iti.presentation.R
import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.ui.theme.SuccessContainerDark
import com.iti.presentation.ui.theme.SuccessContainerLight
import com.iti.presentation.ui.theme.SuccessDark
import com.iti.presentation.ui.theme.SuccessLight

@Composable
fun AddressItem(
    address: Address,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val isDark = LocalDarkTheme.current
    val containerColor = if (isDark) SuccessContainerDark else SuccessContainerLight
    val successColor = if (isDark) SuccessDark else SuccessLight

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = address.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (address.isDefault) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(containerColor)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.address_badge_default),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = successColor
                            )
                        }
                    }
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.address_more_options_desc),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        if (!address.isDefault) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.address_menu_set_default)) },
                                onClick = {
                                    showMenu = false
                                    onSetDefault()
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.address_menu_delete)) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Body address details
            Text(
                text = address.street,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (address.city.isNotEmpty()) {
                Text(
                    text = address.city,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (address.postalCode.isNotEmpty()) {
                Text(
                    text = address.postalCode,
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
}

@Preview(name = "Light Mode - Default Address")
@Composable
private fun AddressItemDefaultLightPreview() {
    ShopIQTheme(darkTheme = false) {
        AddressItem(
            address = Address(
                id = "1",
                name = "Home",
                street = "123 El Horreya Road",
                city = "Maadi, Cairo",
                postalCode = "11728",
                country = "Egypt",
                latitude = 30.0444,
                longitude = 31.2357,
                isDefault = true
            ),
            onDelete = {},
            onSetDefault = {}
        )
    }
}

@Preview(name = "Dark Mode - Standard Address")
@Composable
private fun AddressItemStandardDarkPreview() {
    ShopIQTheme(darkTheme = true) {
        AddressItem(
            address = Address(
                id = "2",
                name = "Work",
                street = "Smart Village, Building B12",
                city = "Giza",
                postalCode = "12577",
                country = "Egypt",
                latitude = 30.0768,
                longitude = 31.0189,
                isDefault = false
            ),
            onDelete = {},
            onSetDefault = {}
        )
    }
}
