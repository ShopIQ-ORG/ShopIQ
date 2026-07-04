package com.iti.presentation.screens.products.checkout

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.presentation.ui.theme.SearchFieldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlinePaymentScreen(
    onNavigateBack: () -> Unit,
    onPaySecurely: () -> Unit
) {
    var selectedOption by remember { mutableStateOf("Card") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Online Payment",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select a payment option",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(20.dp))

                OnlinePaymentOption(
                    title = "Credit / Debit Card",
                    subtitle = "Visa, Mastercard, Rupay & more",
                    icon = Icons.Default.CreditCard,
                    isSelected = selectedOption == "Card",
                    onClick = { selectedOption = "Card" }
                )

                OnlinePaymentOption(
                    title = "UPI",
                    subtitle = "Pay using any UPI app",
                    icon = Icons.Default.QrCode,
                    isSelected = selectedOption == "UPI",
                    onClick = { selectedOption = "UPI" }
                )

                OnlinePaymentOption(
                    title = "Wallets",
                    subtitle = "PayPal, Apple Pay & more",
                    icon = Icons.Default.Wallet,
                    isSelected = selectedOption == "Wallets",
                    onClick = { selectedOption = "Wallets" }
                )

                OnlinePaymentOption(
                    title = "Net Banking",
                    subtitle = "All major banks supported",
                    icon = Icons.Default.AccountBalance,
                    isSelected = selectedOption == "NetBanking",
                    onClick = { selectedOption = "NetBanking" }
                )
            }

            Button(
                onClick = onPaySecurely,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Pay Securely",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun OnlinePaymentOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else SearchFieldLight,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
