//
//  PaymentMethodScreen.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.screens.payment.PaymentMethodContract.Intent
import com.iti.presentation.screens.payment.PaymentMethodContract.PaymentMethodType
import com.iti.presentation.screens.payment.components.PaymentMethodCard
import com.iti.presentation.screens.payment.components.LogoContainer
import com.iti.presentation.ui.theme.SearchFieldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    viewModel: PaymentMethodViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNextStep: (PaymentMethodType) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // Handle Side Effects (Navigation)
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is PaymentMethodContract.Effect.NavigateBack -> onNavigateBack()
                is PaymentMethodContract.Effect.NavigateToNextStep -> onNavigateToNextStep(effect.methodType)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Payment Method",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.sendIntent(Intent.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { viewModel.sendIntent(Intent.ContinueClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose a payment method",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            //  Card: Cash on Delivery (COD)
            PaymentMethodCard(
                title = "Cash on Delivery (COD)",
                subtitle = "Pay with cash when your order is delivered.",
                icon = rememberVectorPainter(Icons.Outlined.Payments),
                type = PaymentMethodType.COD,
                isSelected = state.selectedMethod == PaymentMethodType.COD,
                onSelect = { viewModel.sendIntent(Intent.SelectPaymentMethod(it)) }
            ) {
                // Inner Alert/Info Box for COD Limit
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = SearchFieldLight
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

            //  Card: Online Payment
            PaymentMethodCard(
                title = "Online Payment",
                subtitle = "Pay securely using your card, wallet or other methods.",
                icon = rememberVectorPainter(Icons.Outlined.AccountBalanceWallet),
                type = PaymentMethodType.ONLINE,
                isSelected = state.selectedMethod == PaymentMethodType.ONLINE,
                onSelect = { viewModel.sendIntent(Intent.SelectPaymentMethod(it)) }
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
                        Text(text = "Pay", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
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
    }
}
