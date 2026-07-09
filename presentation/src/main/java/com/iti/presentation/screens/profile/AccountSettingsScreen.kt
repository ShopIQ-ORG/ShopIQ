//
//  AccountSettingsScreen.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.profile

import android.app.Activity
import androidx.activity.compose.LocalActivity
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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.res.stringResource
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.core.os.LocaleListCompat
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.iti.presentation.components.UnauthorizedDialog
import com.iti.presentation.util.LocaleManager

@Composable
fun AccountSettingsScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
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
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.sendIntent(ProfileContract.Intent.LoadProfile)
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileContract.Effect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message.resolve(context))
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
        onLogout = onLogout,
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
    onLogout: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToLocalizationCurrency: () -> Unit,
    onNavigateToAddressManagement: () -> Unit,
    onNavigateToOrders: () -> Unit,
    bottomPadding: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showLanguageBottomSheet by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.profile_sign_out_title),
            message = stringResource(R.string.profile_sign_out_msg),
            confirmText = stringResource(R.string.profile_sign_out_title),
            dismissText = stringResource(R.string.profile_cancel),
            onConfirm = {
                showLogoutDialog = false
                onIntent(ProfileContract.Intent.ClearError)
                onIntent(ProfileContract.Intent.Logout)
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showAuthDialog) {
        UnauthorizedDialog(
            onDismiss = { showAuthDialog = false },
            onLogin = {
                showAuthDialog = false
                onLogout()
            }
        )
    }

    if (showLanguageBottomSheet) {
        val context = LocalContext.current
        val activity = context as? Activity
        val currentLanguage = LocaleManager.currentLanguageTag(context)

        ModalBottomSheet(
            onDismissRequest = { showLanguageBottomSheet = false },
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.select_language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LanguageItemRow(
                    languageName = "English",
                    isSelected = currentLanguage == LocaleManager.LANGUAGE_ENGLISH,
                    onClick = {
                        showLanguageBottomSheet = false
                        activity?.let {
                            LocaleManager.setAppLanguage(
                                it,
                                LocaleManager.LANGUAGE_ENGLISH
                            )
                        }
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))

                LanguageItemRow(
                    languageName = "العربية",
                    isSelected = currentLanguage == LocaleManager.LANGUAGE_ARABIC,
                    onClick = {
                        showLanguageBottomSheet = false
                        activity?.let {
                            LocaleManager.setAppLanguage(
                                it,
                                LocaleManager.LANGUAGE_ARABIC
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    val isGuest = state.user is User.GuestUser || state.user == null
    val displayName = when (val u = state.user) {
        is User.AuthenticatedUser -> u.fullName
        User.GuestUser -> stringResource(R.string.profile_guest)
        null -> "Loading..."
    }
    val displayEmail = when (val u = state.user) {
        is User.AuthenticatedUser -> u.email
        else -> stringResource(R.string.profile_sign_in_prompt)
    }
    val avatarUrl = when (val u = state.user) {
        is User.AuthenticatedUser -> u.avatarUrl
        else -> null
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_settings_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
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
                fullName = displayName,
                email = displayEmail,
                avatarUrl = avatarUrl,
                onClick = {
                    if (isGuest) showAuthDialog = true else onNavigateToEditProfile()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Account Settings
            Text(
                text = stringResource(R.string.profile_settings_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.5f
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.LocationOn,
                        title = stringResource(R.string.profile_manage_addresses),
                        onClick = {
                            if (isGuest) showAuthDialog = true else onNavigateToAddressManagement()
                        }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    SettingsRowItem(
                        icon = Icons.Default.ShoppingCart,
                        title = stringResource(R.string.profile_order_history),
                        onClick = {
                            if (isGuest) showAuthDialog = true else onNavigateToOrders()
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    SettingsRowItem(
                        icon = Icons.Default.Settings,
                        title = stringResource(R.string.profile_localization_currency),
                        onClick = onNavigateToLocalizationCurrency
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    SettingsRowItem(
                        icon = Icons.Default.Language,
                        title = stringResource(R.string.language_title),
                        onClick = { showLanguageBottomSheet = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Preferences & Flagship additions
            Text(
                text = stringResource(R.string.profile_preferences),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.5f
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsSwitchItem(
                        icon = Icons.Default.Settings,
                        title = stringResource(R.string.profile_dark_theme),
                        checked = isDarkTheme,
                        onCheckedChange = { ThemeManager.toggleTheme() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login / Logout Button
            if (isGuest) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogout() }
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.profile_sign_in_title),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = stringResource(R.string.profile_sign_in_desc),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            } else {
                androidx.compose.material3.OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.profile_sign_out_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
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
            onLogout = {},
            onNavigateToEditProfile = {},
            onNavigateToLocalizationCurrency = {},
            onNavigateToAddressManagement = {},
            onNavigateToOrders = {},
            bottomPadding = 0.dp
        )
    }
}

@Composable
fun LanguageItemRow(
    languageName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = languageName,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
