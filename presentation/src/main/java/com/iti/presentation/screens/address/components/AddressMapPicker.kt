@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.iti.presentation.screens.address.components

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.screens.address.AddressContract
import com.iti.presentation.screens.address.AddressViewModel
import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.ui.theme.SuccessDark
import com.iti.presentation.ui.theme.SuccessLight
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressMapPicker(
    initialLatitude: Double,
    initialLongitude: Double,
    onLocationConfirmed: (latitude: Double, longitude: Double) -> Unit,
    onBackClick: () -> Unit,
    viewModel: AddressViewModel?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }

    val isDark = LocalDarkTheme.current
    val successColor = if (isDark) SuccessDark else SuccessLight

    val state = viewModel?.state?.collectAsState()?.value ?: AddressContract.State()

    // Configure user agent for osmdroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(17.0)
            controller.setCenter(GeoPoint(initialLatitude, initialLongitude))
        }
    }

    // Handle MapView lifecycles
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, mapView) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
            mapView.onDetach()
        }
    }

    val performSearch = {
        if (searchQuery.isNotBlank() && viewModel != null) {
            isSearching = true
            searchError = null
            keyboardController?.hide()
            viewModel.sendIntent(AddressContract.Intent.ClearSuggestions)
            coroutineScope.launch {
                val resultCoords = viewModel.searchLocationByName(searchQuery)
                isSearching = false
                if (resultCoords != null) {
                    mapView.controller.animateTo(GeoPoint(resultCoords.latitude, resultCoords.longitude))
                } else {
                    searchError = context.getString(R.string.address_error_location_not_found)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Interactive Map
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            )

            // Center Pin Overlay (Map pans beneath this pin)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Drop Shadow/Circle at target point
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.2f))
                )
                // Floating Marker Pin
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = successColor,
                    modifier = Modifier
                        .size(48.dp)
                        .offset(y = (-24).dp)
                )
            }

            // Top Search Bar & Back Actions & Autocomplete suggestions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        viewModel?.sendIntent(AddressContract.Intent.ClearSuggestions)
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.address_cancel_btn),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it 
                            searchError = null
                            viewModel?.sendIntent(AddressContract.Intent.SearchQueryChanged(it))
                        },
                        placeholder = { Text(stringResource(R.string.address_search_hint)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = { performSearch() }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = { performSearch() }) {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.address_gps_search),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Autocomplete Suggestions Card
                val suggestions = state.searchSuggestions
                if (suggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            suggestions.forEach { suggestion ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            text = suggestion.displayName,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.bodyMedium
                                        ) 
                                    },
                                    onClick = {
                                        mapView.controller.animateTo(GeoPoint(suggestion.latitude, suggestion.longitude))
                                        searchQuery = suggestion.displayName
                                        viewModel?.sendIntent(AddressContract.Intent.ClearSuggestions)
                                        keyboardController?.hide()
                                    }
                                )
                            }
                        }
                    }
                }

                searchError?.let { errorText ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }

            // Bottom Confirmation Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.BottomCenter)
            ) {
                ShopIQButton(
                    text = stringResource(R.string.address_btn_confirm_location),
                    onClick = {
                        viewModel?.sendIntent(AddressContract.Intent.ClearSuggestions)
                        val centerPoint = mapView.mapCenter
                        onLocationConfirmed(centerPoint.latitude, centerPoint.longitude)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Light Mode")
@Composable
private fun AddressMapPickerLightPreview() {
    ShopIQTheme(darkTheme = false) {
        AddressMapPicker(
            initialLatitude = 30.0444,
            initialLongitude = 31.2357,
            onLocationConfirmed = { _, _ -> },
            onBackClick = {},
            viewModel = null
        )
    }
}

@Preview(name = "Dark Mode")
@Composable
private fun AddressMapPickerDarkPreview() {
    ShopIQTheme(darkTheme = true) {
        AddressMapPicker(
            initialLatitude = 30.0444,
            initialLongitude = 31.2357,
            onLocationConfirmed = { _, _ -> },
            onBackClick = {},
            viewModel = null
        )
    }
}
