//
//  LocalizationCurrencyScreen.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.domain.models.Currency
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.screens.profile.components.ExchangeTrendChart
import androidx.compose.ui.res.stringResource
import com.iti.presentation.R
import java.util.Locale
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalizationCurrencyScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileContract.Effect.NavigateBack -> onNavigateBack()
                is ProfileContract.Effect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ProfileContract.Effect.ShowCurrencyUpdatedMessage -> {
                    val message = context.getString(R.string.currency_updated_to, effect.currencyCode)
                    snackbarHostState.showSnackbar(message)
                }
                else -> Unit
            }
        }
    }

    LocalizationCurrencyContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::sendIntent,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalizationCurrencyContent(
    state: ProfileContract.State,
    snackbarHostState: SnackbarHostState,
    onIntent: (ProfileContract.Intent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currencyDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            BackTopBar(
                title = stringResource(R.string.profile_localization_currency),
                onBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Section: Current Currency Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { currencyDropdownExpanded = true }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.profile_localization_currency),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.currency_current),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = getFlagEmoji(state.selectedCurrency.code),
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "${state.selectedCurrency.code} - ${state.selectedCurrency.name}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Change Currency",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Dropdown menu to change current currency
                DropdownMenu(
                    expanded = currencyDropdownExpanded,
                    onDismissRequest = { currencyDropdownExpanded = false }
                ) {
                    state.popularCurrencies.forEach { currency ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = getFlagEmoji(currency.code), fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "${currency.code} - ${currency.name}")
                                }
                            },
                            onClick = {
                                onIntent(ProfileContract.Intent.ChangeCurrency(currency.code))
                                currencyDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section: Popular Currencies
            Text(
                text = stringResource(R.string.currency_popular),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    state.popularCurrencies.forEachIndexed { index, currency ->
                        PopularCurrencyRow(
                            flagEmoji = getFlagEmoji(currency.code),
                            code = currency.code,
                            name = currency.name,
                            symbol = currency.symbol,
                            rateToUsd = currency.rateToUsd,
                            onClick = {
                                onIntent(ProfileContract.Intent.ChangeCurrency(currency.code))
                            }
                        )
                        if (index < state.popularCurrencies.size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Refresh rate container
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.currency_rates_live),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = state.exchangeRatesLastUpdated.ifBlank { "May 12, 2024 09:41 AM" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                if (state.exchangeRateLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    IconButton(onClick = { onIntent(ProfileContract.Intent.RefreshExchangeRates) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Rates",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.currency_powered_by),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 28.dp)
            )

            // Section: Exchange Trend Chart (Dynamic for active selected currency)
            Text(
                text = "${state.selectedCurrency.code} " + stringResource(R.string.currency_trend_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ExchangeTrendChart(
                        history = state.exchangeRateHistory,
                        primaryColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun PopularCurrencyRow(
    flagEmoji: String,
    code: String,
    name: String,
    symbol: String,
    rateToUsd: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = flagEmoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "$code - $name",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "1 USD = ${String.format(Locale.US, "%.2f", rateToUsd)} $code",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = "$symbol ${String.format(Locale.US, "%.2f", rateToUsd)}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun getFlagEmoji(code: String): String {
    return when (code) {
        "USD" -> "🇺🇸"
        "EGP" -> "🇪🇬"
        "EUR" -> "🇪🇺"
        "GBP" -> "🇬🇧"
        "INR" -> "🇮🇳"
        "AED" -> "🇦🇪"
        "SAR" -> "🇸🇦"
        else -> "🏳️"
    }
}

@Preview(showBackground = true)
@Composable
fun LocalizationCurrencyScreenPreview() {
    MaterialTheme {
        LocalizationCurrencyContent(
            state = ProfileContract.State(
                selectedCurrency = Currency("USD", "US Dollar", "$", 1.0),
                popularCurrencies = listOf(
                    Currency("EUR", "Euro", "€", 0.92),
                    Currency("GBP", "British Pound", "£", 0.79),
                    Currency("INR", "Indian Rupee", "₹", 83.12),
                    Currency("AED", "UAE Dirham", "د.إ", 3.67)
                ),
                exchangeRatesLastUpdated = "May 12, 2024 09:41 AM",
                exchangeRateHistory = listOf(
                    "Mon" to 0.91,
                    "Tue" to 0.92,
                    "Wed" to 0.92,
                    "Thu" to 0.93,
                    "Fri" to 0.92,
                    "Sat" to 0.92,
                    "Sun" to 0.92
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
            onNavigateBack = {}
        )
    }
}
