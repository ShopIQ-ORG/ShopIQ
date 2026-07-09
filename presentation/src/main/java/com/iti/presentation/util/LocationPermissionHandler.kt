package com.iti.presentation.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.iti.presentation.R

@Composable
fun LocationPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    triggerRequest: Boolean,
) {
    val context = LocalContext.current
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var denialCount by rememberSaveable { mutableStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineGranted || coarseGranted) {
            denialCount = 0
            onPermissionGranted()
        } else {
            denialCount++
            if (denialCount == 1) {
                showRationaleDialog = true
            } else {
                showSettingsDialog = true
            }
            onPermissionDenied()
        }
    }

    LaunchedEffect(triggerRequest) {
        if (triggerRequest) {
            val fineGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val coarseGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (fineGranted || coarseGranted) {
                denialCount = 0
                onPermissionGranted()
            } else {
                if (denialCount >= 2) {
                    showSettingsDialog = true
                } else {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }
    }

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text(text = stringResource(R.string.address_permission_required_title)) },
            text = { Text(text = stringResource(R.string.address_permission_required_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationaleDialog = false
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                ) {
                    Text(stringResource(R.string.address_permission_retry_btn))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text(text = stringResource(R.string.address_permission_denied_title)) },
            text = { Text(text = stringResource(R.string.address_permission_denied_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSettingsDialog = false
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text(stringResource(R.string.address_permission_settings_btn))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
