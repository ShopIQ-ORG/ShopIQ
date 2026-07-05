package com.iti.presentation.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R
import com.iti.presentation.util.NetworkMonitor

private val ILLUSTRATION_SIZE = 260.dp
private val BUTTON_HEIGHT = 52.dp
private val SCREEN_PADDING = 24.dp
private val ELEMENT_SPACING_LARGE = 24.dp
private val ELEMENT_SPACING_MEDIUM = 16.dp
private val ELEMENT_SPACING_SMALL = 8.dp
private val CORNER_RADIUS = 12.dp

@Composable
fun NoInternetScreen(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val networkMonitor = remember { NetworkMonitor(context) }
    val isConnected by networkMonitor.isConnected.collectAsState(initial = networkMonitor.isCurrentlyConnected())

    LaunchedEffect(isConnected) {
        if (isConnected) {
            onRetry()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SCREEN_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.no_internet),
            contentDescription = stringResource(id = R.string.no_internet_title),
            modifier = Modifier.size(ILLUSTRATION_SIZE),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(ELEMENT_SPACING_LARGE))

        Text(
            text = stringResource(id = R.string.no_internet_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ELEMENT_SPACING_SMALL))

        Text(
            text = stringResource(id = R.string.no_internet_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = SCREEN_PADDING)
        )

        Spacer(modifier = Modifier.height(ELEMENT_SPACING_LARGE))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(BUTTON_HEIGHT),
            shape = RoundedCornerShape(CORNER_RADIUS),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(ELEMENT_SPACING_SMALL))
            Text(
                text = stringResource(id = R.string.no_internet_btn_retry),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        Spacer(modifier = Modifier.height(ELEMENT_SPACING_MEDIUM))

        OutlinedButton(
            onClick = { openNetworkSettings(context) },
            modifier = Modifier
                .fillMaxWidth()
                .height(BUTTON_HEIGHT),
            shape = RoundedCornerShape(CORNER_RADIUS)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(ELEMENT_SPACING_SMALL))
            Text(
                text = stringResource(id = R.string.no_internet_btn_settings),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

private fun openNetworkSettings(context: Context) {
    try {
        context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    } catch (e: Exception) {
        try {
            context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (ignored: Exception) {}
    }
}
