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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiHistoryScreen(
    viewModel: AiHistoryViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AiHistoryContract.Effect.NavigateBack -> onNavigateBack()
                is AiHistoryContract.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message.resolve(context), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (state.showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.sendIntent(AiHistoryContract.Intent.DismissDeleteDialog) },
            title = { Text(text = stringResource(id = R.string.delete_history_confirm_title)) },
            text = { Text(text = stringResource(id = R.string.delete_history_confirm_message)) },
            confirmButton = {
                TextButton(onClick = { viewModel.sendIntent(AiHistoryContract.Intent.ConfirmDeleteAll) }) {
                    Text(text = stringResource(id = R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.sendIntent(AiHistoryContract.Intent.DismissDeleteDialog) }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.history_title),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.sendIntent(AiHistoryContract.Intent.DeleteAllClicked) }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(id = R.string.delete_all),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.delete_all),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            Box(modifier = Modifier.padding(16.dp)) {
                SearchBar(
                    value = state.searchQuery,
                    placeholderText = stringResource(id = R.string.search_conversations_hint),
                    onValueChanged = { viewModel.sendIntent(AiHistoryContract.Intent.SearchQueryChanged(it)) }
                )
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
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

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
                ) {
                    if (today.isNotEmpty()) {
                        item { HeaderTitle(stringResource(id = R.string.today)) }
                        items(today) { item ->
                            ConversationCard(
                                item = item,
                                timeFormatter = SimpleDateFormat("hh:mm a", LocalLocale.current.platformLocale),
                                onClick = { viewModel.sendIntent(AiHistoryContract.Intent.ConversationClicked(item)) }
                            )
                        }
                    }
                    if (yesterday.isNotEmpty()) {
                        item { HeaderTitle(stringResource(id = R.string.yesterday)) }
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
                        item { HeaderTitle(stringResource(id = R.string.older)) }
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
fun HeaderTitle(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
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
            com.iti.presentation.screens.ai.components.AiAvatar(
                size = 48.dp,
                iconSize = 36.dp
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
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}
