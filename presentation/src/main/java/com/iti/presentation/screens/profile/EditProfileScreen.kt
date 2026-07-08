//
//  EditProfileScreen.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.profile

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.iti.domain.models.User
import com.iti.presentation.R
import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.ShopIQButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var topSnackbarMessage by remember { mutableStateOf<String?>(null) }
    var topSnackbarIsSuccess by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileContract.Effect.NavigateBack -> onNavigateBack()
                is ProfileContract.Effect.ShowMessage -> {
                    topSnackbarMessage = effect.message.resolve(context)
                    topSnackbarIsSuccess = false
                    scope.launch {
                        delay(3000)
                        topSnackbarMessage = null
                    }
                }
                ProfileContract.Effect.ShowSuccessMessage -> {
                    topSnackbarMessage = context.getString(R.string.profile_updated_successfully)
                    topSnackbarIsSuccess = true
                    scope.launch {
                        delay(3000)
                        topSnackbarMessage = null
                    }
                }
                else -> Unit
            }
        }
    }

    EditProfileContent(
        state = state,
        snackbarHostState = snackbarHostState,
        topSnackbarMessage = topSnackbarMessage,
        topSnackbarIsSuccess = topSnackbarIsSuccess,
        onTopSnackbarDismiss = { topSnackbarMessage = null },
        onIntent = viewModel::sendIntent,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    state: ProfileContract.State,
    snackbarHostState: SnackbarHostState,
    onIntent: (ProfileContract.Intent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    topSnackbarMessage: String? = null,
    topSnackbarIsSuccess: Boolean = false,
    onTopSnackbarDismiss: () -> Unit = {}
) {
    val context = LocalContext.current

    val authUser = state.user as? User.AuthenticatedUser
    android.util.Log.d("EditProfile", "Initial authUser: $authUser")

    var fullName by remember { mutableStateOf(authUser?.fullName ?: "") }
    var email by remember { mutableStateOf(authUser?.email ?: "") }
    var phone by remember { mutableStateOf(authUser?.phone ?: "") }
    var dateOfBirth by remember { mutableStateOf(authUser?.dateOfBirth ?: "") }
    var gender by remember { mutableStateOf(authUser?.gender ?: "") }
    // Use state.user as key so avatarUrl resets when user data loads (important for Google Sign-In photo)
    var avatarUrl by remember(state.user) { mutableStateOf(authUser?.avatarUrl ?: "") }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.user) {
        android.util.Log.d("EditProfile", "state.user changed to: ${state.user}")
        val u = state.user as? User.AuthenticatedUser
        u?.let {
            android.util.Log.d("EditProfile", "Updating fields with user data: name=${it.fullName}, avatar=${it.avatarUrl}")
            fullName = it.fullName
            email = it.email
            phone = it.phone
            dateOfBirth = it.dateOfBirth ?: ""
            gender = it.gender ?: ""
            avatarUrl = it.avatarUrl ?: ""
        }
    }

    val isFormValid by remember {
        derivedStateOf {
            fullName.isNotBlank() &&
                    email.isNotBlank() &&
                    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                    fullNameError == null &&
                    emailError == null &&
                    phoneError == null
        }
    }

    // Date Picker Setup
    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
                dateOfBirth = sdf.format(selectedCalendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Gender selection toggle
    var genderExpanded by remember { mutableStateOf(false) }

    val photoPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val file = java.io.File(context.filesDir, "avatar_${System.currentTimeMillis()}.jpg")
                    val outputStream = java.io.FileOutputStream(file)
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()
                    avatarUrl = file.absolutePath
                }
            } catch (e: Exception) {
                // Ignore if saving fails, fallback to uri
                avatarUrl = uri.toString()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BackTopBar(
                title = stringResource(R.string.profile_edit_profile),
                onBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar Circle with Camera icon overlay
                Box(
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .size(120.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                    ) {
                        if (avatarUrl.isNotBlank()) {
                            val isDark = LocalDarkTheme.current
                            val fallback = if (isDark) R.drawable.logo_dark else R.drawable.logo_light

                            // Support: local file path "/" , file URI "file://", and remote URLs "http/https" (e.g. Google photo)
                            val modelData: Any = when {
                                avatarUrl.startsWith("/") -> java.io.File(avatarUrl)
                                avatarUrl.startsWith("file://") -> android.net.Uri.parse(avatarUrl)
                                avatarUrl.startsWith("http://") || avatarUrl.startsWith("https://") -> avatarUrl
                                else -> avatarUrl
                            }
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(modelData)
                                    .crossfade(true)
                                    .error(fallback)
                                    .fallback(fallback)
                                    .build(),
                                contentDescription = stringResource(R.string.profile_edit_profile),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            val firstLetter = if (fullName.isNotBlank()) fullName.trim().first().uppercaseChar().toString() else "?"
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = firstLetter,
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = 40.sp
                                    )
                                )
                            }
                        } // Close if-else block
                    } // Close inner Box

                    // Camera icon overlay button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable {
                                photoPickerLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.profile_edit_profile),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Input: Full Name
                ProfileInputField(
                    label = stringResource(R.string.full_name),
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        fullNameError = if (it.isBlank()) context.getString(R.string.error_full_name_required) else null
                    },
                    placeholder = stringResource(R.string.full_name),
                    errorMessage = fullNameError
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Input: Email
                ProfileInputField(
                    label = stringResource(R.string.email_address),
                    value = email,
                    onValueChange = {}, // Disabled
                    placeholder = stringResource(R.string.email_address),
                    errorMessage = null,
                    readOnly = true,
                    enabled = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Uneditable",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Input: Phone Number
                ProfileInputField(
                    label = stringResource(R.string.phone_number),
                    value = phone,
                    onValueChange = {
                        phone = it
                        phoneError = if (it.isNotBlank() && it.length < 7) {
                            context.getString(R.string.error_invalid_phone_length)
                        } else {
                            null
                        }
                    },
                    placeholder = stringResource(R.string.phone_number),
                    errorMessage = phoneError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Input: Date of Birth
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() }
                ) {
                    ProfileInputField(
                        label = stringResource(R.string.date_of_birth),
                        value = dateOfBirth,
                        onValueChange = {},
                        placeholder = stringResource(R.string.select_date_of_birth),
                        readOnly = true,
                        enabled = false,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(R.string.select_date_of_birth),
                                modifier = Modifier.clickable { datePickerDialog.show() }
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Input: Gender
                Box(modifier = Modifier.fillMaxWidth()) {
                    val genderLabelMap = mapOf(
                        "Male" to stringResource(R.string.gender_male),
                        "Female" to stringResource(R.string.gender_female),
                        "Other" to stringResource(R.string.gender_other)
                    )
                    ProfileInputField(
                        label = stringResource(R.string.gender),
                        value = genderLabelMap[gender] ?: gender,
                        onValueChange = {},
                        placeholder = stringResource(R.string.select_gender),
                        readOnly = true,
                        enabled = false,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = stringResource(R.string.select_gender),
                                modifier = Modifier.clickable { genderExpanded = true }
                            )
                        },
                        modifier = Modifier.clickable { genderExpanded = true }
                    )
                    DropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        listOf("Male", "Female", "Other").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(genderLabelMap[option] ?: option) },
                                onClick = {
                                    gender = option
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Save Changes Button
                val isModified = fullName != (state.user as? User.AuthenticatedUser)?.fullName ||
                        phone != state.user.phone ||
                        dateOfBirth != state.user.dateOfBirth ||
                        gender != state.user.gender ||
                        avatarUrl != (state.user.avatarUrl ?: "")

                ShopIQButton(
                    text = stringResource(R.string.save_changes),
                    onClick = {
                        if (isFormValid && isModified) {
                            onIntent(
                                ProfileContract.Intent.UpdateProfile(
                                    fullName = fullName,
                                    email = email,
                                    phone = phone,
                                    dateOfBirth = dateOfBirth,
                                    gender = gender,
                                    avatarUrl = avatarUrl
                                )
                            )
                        }
                    },
                    enabled = isFormValid && isModified,
                    isLoading = state.isUpdatingProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }

            // ✅ Top snackbar overlay (same pattern as AddressScreen)
            AnimatedVisibility(
                visible = topSnackbarMessage != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                androidx.compose.material3.Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    color = if (topSnackbarIsSuccess) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTopSnackbarDismiss() }
                ) {
                    Text(
                        text = topSnackbarMessage ?: "",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        } // close Box
    }
}

@Composable
fun ProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    readOnly: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            enabled = enabled,
            readOnly = readOnly,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(12.dp),
            isError = errorMessage != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                disabledTextColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    MaterialTheme {
        EditProfileContent(
            state = ProfileContract.State(
                user = User.AuthenticatedUser(
                    uid = "1",
                    fullName = "John Doe",
                    email = "john.doe@email.com",
                    phone = "+44 7700 900123",
                    dateOfBirth = "May 12, 1995",
                    gender = "Male"
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
            onNavigateBack = {}
        )
    }
}