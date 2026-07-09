package com.iti.presentation.screens.ai.history

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.foundation.layout.height
import com.valentinilk.shimmer.shimmer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R
import com.iti.presentation.components.SearchBar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale
import com.iti.presentation.components.ConfirmationDialog
import com.iti.presentation.screens.ai.components.AiAvatar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiHistoryScreen(
    viewModel: AiHistoryViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AiHistoryContract.Effect.NavigateBack -> onNavigateBack()
                is AiHistoryContract.Effect.ShowToast -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(effect.message.resolve(context))
                    }
                }
            }
        }
    }

    if (state.showDeleteConfirmDialog) {
        ConfirmationDialog(
            title = stringResource(id = R.string.delete_history_confirm_title),
            message = stringResource(id = R.string.delete_history_confirm_message),
            confirmText = stringResource(id = R.string.delete),
            dismissText = stringResource(id = R.string.cancel),
            onConfirm = { viewModel.sendIntent(AiHistoryContract.Intent.ConfirmDeleteAll) },
            onDismiss = { viewModel.sendIntent(AiHistoryContract.Intent.DismissDeleteDialog) }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.history_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SearchBar(
                    value = state.searchQuery,
                    placeholderText = stringResource(id = R.string.search_conversations_hint),
                    onValueChanged = { viewModel.sendIntent(AiHistoryContract.Intent.SearchQueryChanged(it)) }
                )
            }

            if (state.isLoading) {
                HistoryShimmerEffect()
            } else if (state.conversations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(id = R.string.history_empty), color = Color.Gray)
                }
            } else {
                // Group by date
                val today = mutableListOf<AiHistoryContract.ConversationItem>()
                val yesterday = mutableListOf<AiHistoryContract.ConversationItem>()
                val older = mutableListOf<AiHistoryContract.ConversationItem>()

                val calendar = Calendar.getInstance()
                val todayDay = calendar.get(Calendar.DAY_OF_YEAR)
                val todayYear = calendar.get(Calendar.YEAR)

                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val yesterdayDay = calendar.get(Calendar.DAY_OF_YEAR)
                val yesterdayYear = calendar.get(Calendar.YEAR)

                state.conversations.forEach { item ->
                    val itemCal = Calendar.getInstance()
                    itemCal.timeInMillis = item.timestamp
                    val itemDay = itemCal.get(Calendar.DAY_OF_YEAR)
                    val itemYear = itemCal.get(Calendar.YEAR)

                    if (itemDay == todayDay && itemYear == todayYear) {
                        today.add(item)
                    } else if (itemDay == yesterdayDay && itemYear == yesterdayYear) {
                        yesterday.add(item)
                    } else {
                        older.add(item)
                    }
                }

                val showClearInToday = today.isNotEmpty()
                val showClearInYesterday = yesterday.isNotEmpty() && today.isEmpty()
                val showClearInOlder = older.isNotEmpty() && today.isEmpty() && yesterday.isEmpty()

                val clearChatButton: @Composable () -> Unit = {
                    Text(
                        text = stringResource(id = R.string.clear_history),
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { viewModel.sendIntent(AiHistoryContract.Intent.DeleteAllClicked) }
                            .padding(4.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
                ) {
                    if (today.isNotEmpty()) {
                        item { HeaderTitle(stringResource(id = R.string.today), if (showClearInToday) clearChatButton else null) }
                        items(today) { item ->
                            ConversationCard(
                                item = item,
                                timeFormatter = SimpleDateFormat("hh:mm a", LocalLocale.current.platformLocale),
                                onClick = { viewModel.sendIntent(AiHistoryContract.Intent.ConversationClicked(item)) }
                            )
                        }
                    }
                    if (yesterday.isNotEmpty()) {
                        item { HeaderTitle(stringResource(id = R.string.yesterday), if (showClearInYesterday) clearChatButton else null) }
                        items(yesterday) { item ->
                            ConversationCard(
                                item = item,
                                timeLabel = stringResource(id = R.string.yesterday),
                                timeFormatter = null,
                                onClick = { viewModel.sendIntent(AiHistoryContract.Intent.ConversationClicked(item)) }
                            )
                        }
                    }
                    if (older.isNotEmpty()) {
                        item { HeaderTitle(stringResource(id = R.string.older), if (showClearInOlder) clearChatButton else null) }
                        items(older) { item ->
                            ConversationCard(
                                item = item,
                                timeFormatter = SimpleDateFormat("MMM dd", LocalLocale.current.platformLocale),
                                onClick = { viewModel.sendIntent(AiHistoryContract.Intent.ConversationClicked(item)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderTitle(title: String, trailingContent: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        trailingContent?.invoke()
    }
}

@Composable
fun ConversationCard(
    item: AiHistoryContract.ConversationItem,
    timeLabel: String? = null,
    timeFormatter: SimpleDateFormat? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AiAvatar(
                size = 50.dp,
                iconSize = 45.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.query,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.aiResponseSnippet,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Time and chevron
            Row(verticalAlignment = Alignment.CenterVertically) {
                val timeStr = timeLabel ?: timeFormatter?.format(Date(item.timestamp)) ?: ""
                Text(
                    text = timeStr,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun HistoryShimmerEffect() {
    val shimmerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shimmer(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(5) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(shimmerColor, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(14.dp)
                                .background(shimmerColor, shape = RoundedCornerShape(4.dp))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .height(12.dp)
                                .background(shimmerColor, shape = RoundedCornerShape(4.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(12.dp)
                            .background(shimmerColor, shape = RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}