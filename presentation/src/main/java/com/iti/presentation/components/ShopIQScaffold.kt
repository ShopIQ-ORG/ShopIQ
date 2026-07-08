package com.iti.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.iti.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopIQScaffold(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.app_name),
    cartItemCount: Int = 0,
    onCartClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    content: @Composable (innerPadding: PaddingValues, scrollBehavior: TopAppBarScrollBehavior) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            snackbarHostState?.let { SnackbarHost(it) }
        },
        topBar = {
            AppTopBar(
                title = title,
                scrollBehavior = scrollBehavior,
                cartItemCount = cartItemCount,
                onMenuClick = onMenuClick,
                onCartClick = onCartClick
            )
        }
    ) { innerPadding ->
        content(innerPadding, scrollBehavior)
    }
}