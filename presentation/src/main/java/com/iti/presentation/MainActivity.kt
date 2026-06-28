package com.iti.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.presentation.ui.theme.ShopIQTheme

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.iti.presentation.main.MainContract
import com.iti.presentation.main.MainViewModel
import com.iti.presentation.onboarding.OnboardingScreen
import com.iti.presentation.onboarding.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel

import com.iti.presentation.splash.SplashScreen

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopIQTheme {
                val mainViewModel: MainViewModel = koinViewModel()
                val mainState by mainViewModel.state.collectAsState()

                when (mainState) {
                    is MainContract.State.Loading -> {
                        SplashScreen()
                    }
                    is MainContract.State.ShowOnboarding -> {
                        val onboardingViewModel: OnboardingViewModel = koinViewModel()
                        OnboardingScreen(
                            viewModel = onboardingViewModel,
                            onNavigateToHome = {
                                mainViewModel.sendIntent(MainContract.Intent.CheckOnboarding)
                            }
                        )
                    }
                    is MainContract.State.ShowHome -> {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = MaterialTheme.colorScheme.background
                        ) { innerPadding ->
                            Greeting(
                                name = "Android",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_light),
            contentDescription = "ShopIQ Logo",
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = "Hello $name!",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ShopIQTheme {
        Greeting("Android")
    }
}