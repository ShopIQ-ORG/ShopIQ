//
//  PaymentMethodContent.kt
//  ShopIQ
//
//  Created by Antigravity on 7/7/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.checkout.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.screens.checkout.PaymentMethodContract.PaymentMethodType

@Composable
fun PaymentMethodContent(
    selectedMethod: PaymentMethodType?,
    onSelectMethod: (PaymentMethodType) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose a payment method",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Card: Cash on Delivery (COD)
            PaymentMethodCard(
                title = "Cash on Delivery (COD)",
                subtitle = "Pay with cash when your order is delivered.",
                icon = rememberVectorPainter(Icons.Outlined.Payments),
                type = PaymentMethodType.COD,
                isSelected = selectedMethod == PaymentMethodType.COD,
                onSelect = { onSelectMethod(it) }
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "Maximum order amount for COD is $500.00",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card: Online Payment
            PaymentMethodCard(
                title = "Online Payment",
                subtitle = "Pay securely using your card, wallet or other methods.",
                icon = rememberVectorPainter(Icons.Outlined.AccountBalanceWallet),
                type = PaymentMethodType.ONLINE,
                isSelected = selectedMethod == PaymentMethodType.ONLINE,
                onSelect = { onSelectMethod(it) }
            ) {
                // Partner Logos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Visa
                    LogoContainer {
                        Text(text = "VISA", color = Color(0xFF1A1F71), fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }

                    // Mastercard
                    LogoContainer {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(12.dp).background(Color(0xFFEB001B), CircleShape))
                            Box(modifier = Modifier.size(12.dp).offset(x = (-4).dp).background(Color(0xFFFFAB00).copy(alpha = 0.8f), CircleShape))
                        }
                    }

                    // PayPal
                    LogoContainer {
                        Text(text = "PayPal", color = Color(0xFF003087), fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }

                    // Apple Pay
                    LogoContainer {
                        Text(text = "Pay", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }

                    Text(
                        text = "+ More",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        Button(
            onClick = onContinue,
            enabled = selectedMethod != null,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Continue",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun LogoContainer(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 0.5.dp
    ) {
        Box(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
            content()
        }
    }
}
