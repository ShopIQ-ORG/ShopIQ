//
//  AddressListView.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.address.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.domain.models.Address
import com.iti.presentation.R
import com.iti.presentation.components.ConfirmationDialog
import com.iti.presentation.ui.theme.ShopIQTheme

@Composable
fun AddressListView(
    addresses: List<Address>,
    onDeleteAddress: (String) -> Unit,
    onSetDefaultAddress: (String) -> Unit,
    modifier: Modifier = Modifier,
    onAddressSelected: ((Address) -> Unit)? = null
) {
    var addressToDeleteId by remember { mutableStateOf<String?>(null) }
    var selectedAddress by remember(addresses) {
        mutableStateOf(addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull())
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.address_saved_list_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                             fontWeight = FontWeight.Bold,
                             fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(addresses, key = { it.id }) { address ->
                    AddressItem(
                        address = address,
                        onDelete = { addressToDeleteId = address.id },
                        onSetDefault = { onSetDefaultAddress(address.id) },
                        onAddressSelected = if (onAddressSelected != null) {
                            { selectedAddress = it }
                        } else {
                            null
                        },
                        isSelected = onAddressSelected != null && address.id == selectedAddress?.id,
                        modifier = Modifier.animateItem()
                    )
                }
            }

            if (onAddressSelected != null && selectedAddress != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = { onAddressSelected.invoke(selectedAddress!!) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.checkout_btn_confirm_payment),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        addressToDeleteId?.let { addressId ->
            ConfirmationDialog(
                title = stringResource(R.string.address_delete_dialog_title),
                message = stringResource(R.string.address_delete_dialog_msg),
                confirmText = stringResource(R.string.address_delete_btn),
                dismissText = stringResource(R.string.address_cancel_btn),
                onConfirm = {
                    onDeleteAddress(addressId)
                    addressToDeleteId = null
                },
                onDismiss = {
                    addressToDeleteId = null
                }
            )
        }
    }
}

@Preview(name = "Light Mode")
@Composable
private fun AddressListViewLightPreview() {
    val sampleAddresses = listOf(
        Address("1", "Home", "123 Nile Street", "Maadi, Cairo", "11728", "Egypt", 30.0444, 31.2357, isDefault = true),
        Address("2", "Work", "Smart Village, Building B12", "6th of October, Giza", "12577", "Egypt", 30.0768, 31.0189, isDefault = false)
    )
    ShopIQTheme(darkTheme = false) {
        AddressListView(
            addresses = sampleAddresses,
            onDeleteAddress = {},
            onSetDefaultAddress = {}
        )
    }
}

@Preview(name = "Dark Mode")
@Composable
private fun AddressListViewDarkPreview() {
    val sampleAddresses = listOf(
        Address("1", "Home", "123 Nile Street", "Maadi, Cairo", "11728", "Egypt", 30.0444, 31.2357, isDefault = true),
        Address("2", "Work", "Smart Village, Building B12", "6th of October, Giza", "12577", "Egypt", 30.0768, 31.0189, isDefault = false)
    )
    ShopIQTheme(darkTheme = true) {
        AddressListView(
            addresses = sampleAddresses,
            onDeleteAddress = {},
            onSetDefaultAddress = {}
        )
    }
}
