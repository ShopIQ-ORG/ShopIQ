package com.iti.presentation.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.iti.presentation.onboarding.components.OnboardingIndicator
import com.iti.presentation.onboarding.components.OnboardingSlideContent
import com.iti.presentation.onboarding.model.OnboardingSlide
import com.iti.presentation.util.Constants
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 })

    // Handle side effects (navigation)
    LaunchedEffect(key1 = viewModel.effect) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is OnboardingContract.Effect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    // Sync state current page changes from ViewModel to the UI pager
    LaunchedEffect(key1 = state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    // Sync manual swipes from the UI pager back to the ViewModel state
    LaunchedEffect(key1 = pagerState.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            viewModel.sendIntent(OnboardingContract.Intent.PageChanged(pagerState.currentPage))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header: Skip button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Skip",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable {
                        viewModel.sendIntent(OnboardingContract.Intent.Skip)
                    }
                )
            }

            // Pager for slides
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

            // Bottom Actions Area
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
                Button(
                    onClick = {
                        viewModel.sendIntent(OnboardingContract.Intent.Next)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLastPage) "Get Started" else "Next",
                            style = MaterialTheme.typography.labelLarge
                        )
                        if (!isLastPage) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getOnboardingSlides() = listOf(
    OnboardingSlide(
        imagePath = Constants.ONBOARDING_IMAGE_1,
        title = Constants.ONBOARDING_TITLE_1,
        description = Constants.ONBOARDING_DESC_1
    ),
    OnboardingSlide(
        imagePath = Constants.ONBOARDING_IMAGE_2,
        title = Constants.ONBOARDING_TITLE_2,
        description = Constants.ONBOARDING_DESC_2
    ),
    OnboardingSlide(
        imagePath = Constants.ONBOARDING_IMAGE_3,
        title = Constants.ONBOARDING_TITLE_3,
        description = Constants.ONBOARDING_DESC_3
    )
)
