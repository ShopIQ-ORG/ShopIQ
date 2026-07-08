package com.iti.presentation.screens.ai

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iti.domain.models.User
import com.iti.presentation.R
import com.iti.presentation.screens.ai.components.*
import com.iti.presentation.ui.theme.LocalDarkTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import com.iti.presentation.util.NetworkMonitor
import com.iti.presentation.components.NoInternetScreen
import kotlinx.coroutines.launch

@Composable
fun AiChatScreen(
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit,
    currentUser: User?,
    onAuthClick: () -> Unit,
    bottomPadding: Dp = 0.dp,
    onNavigateToProduct: (Long) -> Unit,
    viewModel: AiChatViewModel = koinViewModel()
) {
    val isDark = LocalDarkTheme.current
    
    // Auth Check
    if (currentUser !is User.AuthenticatedUser) {
        AuthRequiredScreen(onBackClick = onBackClick, onAuthClick = onAuthClick)
        return
    }

    val networkMonitor: NetworkMonitor = koinInject()
    val isConnected by networkMonitor.isConnected.collectAsState(initial = networkMonitor.isCurrentlyConnected())
    val enteredWithConnection = remember { mutableStateOf(networkMonitor.isCurrentlyConnected()) }

    if (!enteredWithConnection.value) {
        NoInternetScreen(
            onRetry = {
                if (networkMonitor.isCurrentlyConnected()) {
                    enteredWithConnection.value = true
                }
            },
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        )
        return
    }

    LaunchedEffect(currentUser) {
        viewModel.sendIntent(AiChatContract.Intent.SetUser(currentUser))
    }

    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    val scrollToTimestamp by AiChatSharedState.scrollToMessageTimestamp.collectAsState()
    
    // Auto-scroll to bottom or to specific message when arriving from history
    LaunchedEffect(state.messages.size, scrollToTimestamp) {
        val targetTs = scrollToTimestamp
        if (targetTs != null && state.messages.isNotEmpty()) {
            val index = state.messages.indexOfFirst { it.timestamp == targetTs }
            if (index != -1) {
                listState.animateScrollToItem(index)
            }
            AiChatSharedState.scrollToMessageTimestamp.value = null
        } else if (state.messages.isNotEmpty() && targetTs == null) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    val context = LocalContext.current
    val imageSearchText = stringResource(id = R.string.ai_image_description)
    val imagePickerErrorText = stringResource(id = R.string.ai_image_picker_error)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                if (bytes != null) {
                    viewModel.sendIntent(
                        AiChatContract.Intent.SendMessage(
                            text = imageSearchText,
                            imageBytes = bytes,
                            attachedImageUrl = uri.toString()
                        )
                    )
                }
            } catch (e: Exception) {
                scope.launch {
                    snackbarHostState.showSnackbar(imagePickerErrorText)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AiChatHeader(
                onBackClick = onBackClick,
                onHistoryClick = onHistoryClick
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (state.messages.isEmpty() && state.isLoading) {
                    // Shimmer layout during initial loading
                    AiChatShimmer(modifier = Modifier.fillMaxSize())
                } else {
                    // Messages List (always starts with Eslam's greeting, welcome screen logo deleted)
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(state.messages) { message ->
                            MessageBubbleRow(
                                message = message,
                                isDark = isDark,
                                onProductClick = onNavigateToProduct
                            )
                        }
                        
                        if (state.isBotTyping) {
                            item {
                                AiTypingIndicator(isDark = isDark)
                            }
                        }
                    }
                }
            }

            // Input bar with attachment support, text support, and background SpeechRecognizer voice support
            ChatInputFooter(
                isDark = isDark,
                onAttachmentClick = { imagePickerLauncher.launch("image/*") },
                onSendMessage = { text ->
                    viewModel.sendIntent(
                        AiChatContract.Intent.SendMessage(text = text)
                    )
                },
                onSendVoiceMessage = { text, duration ->
                    viewModel.sendIntent(
                        AiChatContract.Intent.SendMessage(
                            text = text,
                            voiceDuration = duration
                        )
                    )
                },
                onShowSnackbar = { msg ->
                    scope.launch {
                        snackbarHostState.showSnackbar(msg)
                    }
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)
        )
    }
}
