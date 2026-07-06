//
//  AddressLocationDetected.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.address.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.iti.domain.models.Address
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.ui.theme.SuccessDark
import com.iti.presentation.ui.theme.SuccessLight

@Composable
fun AddressLocationDetected(
    address: Address,
    isFromGps: Boolean,
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

    val hasLocation = address.latitude != 0.0 || address.longitude != 0.0

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(address.latitude, address.longitude), 16f)
    }

    LaunchedEffect(address.latitude, address.longitude) {
        if (hasLocation) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(address.latitude, address.longitude), 16f)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Screen titles matching layout
        Text(
            text = if (isFromGps) stringResource(R.string.address_heading_detected) else stringResource(R.string.address_heading_selected),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )

        Text(
            text = stringResource(R.string.address_subheading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        // Map Preview / Snapshot Container (Box)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
        ) {
            if (hasLocation) {
                // Live preview using Google Maps

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false,
                        compassEnabled = false,
                        mapToolbarEnabled = false
                    ),
                    properties = MapProperties(
                        isMyLocationEnabled = false
                    )
                ) {
                    Marker(
                        state = remember { MarkerState(position = LatLng(address.latitude, address.longitude)) },
                        title = address.name.ifBlank { "Selected Location" }
                    )
                }

                // Transparent overlay to capture clicks and open MapPicker
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onEditLocationClick() }
                )
            } else {
                // Static vector roadmap placeholder with center target
                StaticMapPlaceholder(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onEditLocationClick() }
                )
            }

            // Floating "Edit on map" button in the top right
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clickable { onEditLocationClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EditLocation,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.address_edit_on_map),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black
                    )
                }
            }
        }
        // Detailed Address Information Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isFromGps) stringResource(R.string.address_label_detected) else stringResource(R.string.address_label_selected),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = successColor
                    )
                    
                    if (hasLocation) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE8F5E9))
                                .border(1.dp, Color(0xFFC8E6C9), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.address_accurate_badge),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (hasLocation) address.street else stringResource(R.string.address_tap_to_select_location),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onEditLocationClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (hasLocation) {
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
        }

        // Tags & Details Selection Form
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TagChip(
                    label = homeText,
                    isSelected = selectedTag == "Home",
                    onClick = { selectedTag = "Home" },
                    modifier = Modifier.weight(1f)
                )
                TagChip(
                    label = workText,
                    isSelected = selectedTag == "Work",
                    onClick = { selectedTag = "Work" },
                    modifier = Modifier.weight(1f)
                )
                TagChip(
                    label = otherText,
                    isSelected = selectedTag == "Other",
                    onClick = { selectedTag = "Other" },
                    modifier = Modifier.weight(1f)
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

            Spacer(modifier = Modifier.height(4.dp))

            // Default flag switch card matching layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
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
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        // Dividers & Highlights Bar
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrustItem(
                icon = Icons.Default.GpsFixed,
                title = stringResource(R.string.address_trust_accurate),
                subtitle = stringResource(R.string.address_trust_accurate_subtitle),
                modifier = Modifier.weight(1f)
            )
            VerticalDivider(
                modifier = Modifier.height(40.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            TrustItem(
                icon = Icons.Default.AccessTime,
                title = stringResource(R.string.address_trust_time),
                subtitle = stringResource(R.string.address_trust_time_subtitle),
                modifier = Modifier.weight(1f)
            )
            VerticalDivider(
                modifier = Modifier.height(40.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            TrustItem(
                icon = Icons.Default.Security,
                title = stringResource(R.string.address_trust_secure),
                subtitle = stringResource(R.string.address_trust_secure_subtitle),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Action CTA Button
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
            enabled = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.address_change_anytime),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun StaticMapPlaceholder(modifier: Modifier = Modifier) {
    val isDark = LocalDarkTheme.current
    val gridColor = if (isDark) Color(0xFF2C323E) else Color(0xFFE5E7EB)
    val bgColor = if (isDark) Color(0xFF1E232C) else Color(0xFFF3F4F6)
    val pinColor = if (isDark) SuccessDark else SuccessLight

    Box(
        modifier = modifier
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            // Draw diagonal road 1
            drawLine(
                color = gridColor,
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(width, height),
                strokeWidth = 12.dp.toPx()
            )
            // Draw diagonal road 2
            drawLine(
                color = gridColor,
                start = androidx.compose.ui.geometry.Offset(width, 0f),
                end = androidx.compose.ui.geometry.Offset(0f, height),
                strokeWidth = 8.dp.toPx()
            )
            // Horizontal road
            drawLine(
                color = gridColor,
                start = androidx.compose.ui.geometry.Offset(0f, height / 2f),
                end = androidx.compose.ui.geometry.Offset(width, height / 2f),
                strokeWidth = 10.dp.toPx()
            )
            // Vertical road
            drawLine(
                color = gridColor,
                start = androidx.compose.ui.geometry.Offset(width / 2f, 0f),
                end = androidx.compose.ui.geometry.Offset(width / 2f, height),
                strokeWidth = 6.dp.toPx()
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = pinColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.address_tap_to_select_location),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun TrustItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(name = "Light Mode - Manual (Unselected)")
@Composable
private fun AddressLocationDetectedManualLightPreview() {
    ShopIQTheme(darkTheme = false) {
        AddressLocationDetected(
            address = Address(
                id = "1",
                name = "",
                street = "",
                city = "",
                postalCode = "",
                country = "",
                latitude = 0.0,
                longitude = 0.0
            ),
            isFromGps = false,
            onConfirmClick = { _, _ -> },
            onEditLocationClick = {}
        )
    }
}

@Preview(name = "Light Mode - GPS Selected")
@Composable
private fun AddressLocationDetectedGpsLightPreview() {
    ShopIQTheme(darkTheme = false) {
        AddressLocationDetected(
            address = Address(
                id = "1",
                name = "Home",
                street = "Ahmed Zewail Square",
                city = "Bab Sharqi, Wabour Al Meyah",
                postalCode = "5422015",
                country = "Egypt",
                latitude = 30.0054,
                longitude = 31.2332
            ),
            isFromGps = true,
            onConfirmClick = { _, _ -> },
            onEditLocationClick = {}
        )
    }
}
