package com.iti.presentation.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.components.ShopIQSnackBarHost
import com.iti.presentation.components.showError
import com.iti.presentation.screens.onboarding.components.OnboardingIndicator
import com.iti.presentation.screens.onboarding.components.OnboardingSlideContent
import com.iti.presentation.screens.onboarding.model.OnboardingSlide
import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.util.Constants
import kotlin.math.absoluteValue
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val isDark = LocalDarkTheme.current
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is OnboardingContract.Effect.NavigateToHome -> onNavigateToHome()
                is OnboardingContract.Effect.NavigateToSignIn -> onNavigateToSignIn()
                is OnboardingContract.Effect.ShowError ->
                    snackbarHostState.showError(effect.message.resolve(context))
            }
        }
    }

    LaunchedEffect(key1 = state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    LaunchedEffect(key1 = pagerState.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            viewModel.sendIntent(OnboardingContract.Intent.PageChanged(pagerState.currentPage))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { ShopIQSnackBarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_skip),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable(enabled = !state.isLoading) {
                        viewModel.sendIntent(OnboardingContract.Intent.Skip)
                    }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val slide = getOnboardingSlides()[page]
                val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                val alpha = 0.5f + 0.5f * (1f - pageOffset.coerceIn(0f, 1f))
                val scale = 0.85f + 0.15f * (1f - pageOffset.coerceIn(0f, 1f))

                OnboardingSlideContent(
                    slide = slide,
                    modifier = Modifier.graphicsLayer {
                        this.alpha = alpha
                        this.scaleX = scale
                        this.scaleY = scale
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnboardingIndicator(
                    pageCount = 3,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                val isLastPage = pagerState.currentPage == 2
                ShopIQButton(
                    text = if (isLastPage) stringResource(id = R.string.onboarding_get_started) else stringResource(id = R.string.onboarding_next),
                    onClick = {
                        viewModel.sendIntent(OnboardingContract.Intent.Next)
                    },
                    isLoading = state.isLoading,
                    enabled = !state.isLoading
                )
            }
        }
    }
}

@Composable
private fun getOnboardingSlides() = listOf(
    OnboardingSlide(
        imageRes = R.drawable.onboarding_1,
        title = stringResource(id = R.string.onboarding_title_1),
        description = stringResource(id = R.string.onboarding_desc_1)
    ),
    OnboardingSlide(
        imageRes = R.drawable.onboarding_2,
        title = stringResource(id = R.string.onboarding_title_2),
        description = stringResource(id = R.string.onboarding_desc_2)
    ),
    OnboardingSlide(
        imageRes = R.drawable.onboarding_3,
        title = stringResource(id = R.string.onboarding_title_3),
        description = stringResource(id = R.string.onboarding_desc_3)
    )
)