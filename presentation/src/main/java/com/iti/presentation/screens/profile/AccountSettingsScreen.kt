//
//  AccountSettingsScreen.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.profile

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.domain.models.User
import com.iti.presentation.R
import com.iti.presentation.components.ConfirmationDialog
import com.iti.presentation.screens.profile.components.ProfileHeaderCard
import com.iti.presentation.screens.profile.components.SettingsRowItem
import com.iti.presentation.screens.profile.components.SettingsSwitchItem
import com.iti.presentation.util.ThemeManager
import com.iti.domain.models.Currency

@Composable
fun AccountSettingsScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToLocalizationCurrency: () -> Unit,
    onNavigateToAddressManagement: () -> Unit,
    onNavigateToOrders: () -> Unit,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp
) {
    val state by viewModel.state.collectAsState()
    val isDarkTheme by ThemeManager.isDarkTheme.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileContract.Effect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                else -> Unit
            }
        }
    }

    AccountSettingsContent(
        state = state,
        isDarkTheme = isDarkTheme,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::sendIntent,
        onNavigateBack = onNavigateBack,
        onNavigateToEditProfile = onNavigateToEditProfile,
        onNavigateToLocalizationCurrency = onNavigateToLocalizationCurrency,
        onNavigateToAddressManagement = onNavigateToAddressManagement,
        onNavigateToOrders = onNavigateToOrders,
        bottomPadding = bottomPadding,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsContent(
    state: ProfileContract.State,
    isDarkTheme: Boolean,
    snackbarHostState: SnackbarHostState,
    onIntent: (ProfileContract.Intent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToLocalizationCurrency: () -> Unit,
    onNavigateToAddressManagement: () -> Unit,
    onNavigateToOrders: () -> Unit,
    bottomPadding: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        ConfirmationDialog(
            title = "Logout",
            message = "Are you sure you want to sign out of your account?",
            confirmText = "Logout",
            dismissText = "Cancel",
            onConfirm = {
                showLogoutDialog = false
                onIntent(ProfileContract.Intent.ClearError)
                onNavigateBack()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Account Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                actions = {
                    IconButton(onClick = {
                        onIntent(ProfileContract.Intent.ClearError)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Profile Header Card
            ProfileHeaderCard(
                fullName = state.user?.fullName ?: "John Doe",
                email = state.user?.email ?: "john.doe@email.com",
                avatarUrl = state.user?.avatarUrl,
                onClick = onNavigateToEditProfile
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Account Settings
            Text(
                text = "Account Settings",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.Person,
                        title = "Edit Profile",
                        subtitle = "Name, email, phone & more",
                        onClick = onNavigateToEditProfile
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    SettingsRowItem(
                        icon = Icons.Default.LocationOn,
                        title = "Manage Addresses",
                        subtitle = "Add, edit or remove addresses",
                        onClick = onNavigateToAddressManagement
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    SettingsRowItem(
                        icon = Icons.Default.ShoppingCart,
                        title = "Payment Methods",
                        subtitle = "Saved cards & wallets",
                        onClick = {
                            onIntent(ProfileContract.Intent.ConfirmAddress("", "", "", "", "", false, 0.0, 0.0, "", "")) // dummy or message
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    SettingsRowItem(
                        icon = Icons.Default.ShoppingCart,
                        title = "Order History",
                        subtitle = "View your past orders",
                        onClick = onNavigateToOrders
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    SettingsRowItem(
                        icon = Icons.Default.Settings,
                        title = "Localization & Currency",
                        subtitle = "Language, currency & more",
                        onClick = onNavigateToLocalizationCurrency
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Preferences & Flagship additions
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Alerts, messages & promotions",
                        onClick = { }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    SettingsRowItem(
                        icon = Icons.Default.Info,
                        title = "Help & Support",
                        subtitle = "FAQ, contact support & chat",
                        onClick = { }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    SettingsRowItem(
                        icon = Icons.Default.Lock,
                        title = "Privacy & Security",
                        subtitle = "Password, account deletion & privacy policy",
                        onClick = { }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    SettingsSwitchItem(
                        icon = Icons.Default.Settings,
                        title = "Dark Theme",
                        subtitle = "Change app theme preferences",
                        checked = isDarkTheme,
                        onCheckedChange = { ThemeManager.toggleTheme() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.08f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLogoutDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sign Out",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        )
                        Text(
                            text = "Securely sign out of your current session",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(bottomPadding + 16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountSettingsScreenPreview() {
    MaterialTheme {
        AccountSettingsContent(
            state = ProfileContract.State(
                user = User.AuthenticatedUser(
                    uid = "123",
                    fullName = "John Doe",
                    email = "john.doe@email.com",
                    phone = "+44 7700 900123",
                    dateOfBirth = "May 12, 1995",
                    gender = "Male"
                ),
                selectedCurrency = Currency("USD", "US Dollar", "$", 1.0)
            ),
            isDarkTheme = false,
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
            onNavigateBack = {},
            onNavigateToEditProfile = {},
            onNavigateToLocalizationCurrency = {},
            onNavigateToAddressManagement = {},
            onNavigateToOrders = {},
            bottomPadding = 0.dp
        )
    }
}
