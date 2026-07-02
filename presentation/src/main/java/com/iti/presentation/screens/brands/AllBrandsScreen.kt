//
//  AllBrandsScreen.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 01/07/2026.
//
package com.iti.presentation.screens.brands
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.CartIconWithBadge
import com.iti.presentation.components.NoInternetScreen
import com.iti.presentation.screens.brands.components.AllBrandsShimmer
import com.iti.presentation.screens.brands.components.BrandsContent
import com.iti.presentation.screens.home.viewmodel.CartBadgeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllBrandsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAllProducts: (String) -> Unit,
    onCartClick: () -> Unit,
    viewModel: AllBrandsViewModel = koinViewModel(),
    cartBadgeViewModel: CartBadgeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val cartItemCount by cartBadgeViewModel.cartItemCount.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AllBrandsContract.Effect.NavigateToProducts ->
                    onNavigateToAllProducts(effect.brandName)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BackTopBar(
                title = stringResource(R.string.brands_title),
                onBack = onNavigateBack,
                scrollBehavior = scrollBehavior,
                actions = {
                    CartIconWithBadge(
                        count = cartItemCount,
                        onClick = onCartClick
                    )
                }
            )
        }
    ) { innerPadding ->
        when (val screenState = state.screenState) {
            is AllBrandsContract.ScreenState.Loading -> {
                AllBrandsShimmer(
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is AllBrandsContract.ScreenState.Failure -> {
                NoInternetScreen(
                    onRetry = { viewModel.sendIntent(AllBrandsContract.Intent.Retry) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            is AllBrandsContract.ScreenState.Success -> {
                BrandsContent(
                    brands = state.filteredBrands,
                    query = state.query,
                    onQueryChanged = {
                        viewModel.sendIntent(AllBrandsContract.Intent.QueryChanged(it))
                    },
                    onBrandClick = { brandName ->
                        viewModel.sendIntent(AllBrandsContract.Intent.BrandClicked(brandName))
                    },
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}