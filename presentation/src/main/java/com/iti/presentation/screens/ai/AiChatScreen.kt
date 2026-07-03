package com.iti.presentation.screens.ai

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.domain.models.User
import com.iti.presentation.screens.ai.AiChatContract.ChatMessageUi
import com.iti.presentation.screens.ai.AiChatContract.ChatProductUi
import com.iti.presentation.R
import com.iti.presentation.components.CustomNetworkImage
import com.iti.presentation.ui.theme.LocalDarkTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AiChatScreen(
    onBackClick: () -> Unit,
    currentUser: User?,
    onAuthClick: () -> Unit,
    bottomPadding: Dp = 0.dp,
    viewModel: AiChatViewModel = koinViewModel()
) {
    val isDark = LocalDarkTheme.current
    
    // Auth Check
    if (currentUser !is User.AuthenticatedUser) {
        AuthRequiredScreen(onBackClick = onBackClick, onAuthClick = onAuthClick)
        return
    }

    LaunchedEffect(currentUser) {
        viewModel.sendIntent(AiChatContract.Intent.SetUser(currentUser))
    }

    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    // Image Picker Launcher
    val context = LocalContext.current
    LaunchedEffect(state.error) {
        state.error?.let { err ->
            android.widget.Toast.makeText(context, err, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                if (bytes != null) {
                    viewModel.sendIntent(
                        AiChatContract.Intent.SendMessage(
                            text = "Search by this image.",
                            imageBytes = bytes,
                            attachedImageUrl = uri.toString()
                        )
                    )
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    Scaffold(
        topBar = {
            AiChatHeader(
                onBackClick = onBackClick,
                onResetClick = { /* Clear local history */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding(), bottom = bottomPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (state.messages.isEmpty() && !state.isLoading) {
                // Welcome screen for empty chat state
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(120.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(
                                        color = if (isDark) Color(0xFF3B1E78).copy(alpha = 0.15f) else Color(0xFFE8DDFF).copy(alpha = 0.4f),
                                        shape = CircleShape
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        brush = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF4F46E5))),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_ai),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Hello! I am Eslam",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Your personal shopping assistant. Ask me about products, check stock, or upload images to search!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Suggestion Chips
                        Text(
                            text = "Try asking:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val suggestions = listOf("Sneakers under $100", "Show me bags")
                            suggestions.forEach { suggestion ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(1.dp, if (isDark) Color(0xFF2E3844) else Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                                        .clickable {
                                            viewModel.sendIntent(
                                                AiChatContract.Intent.SendMessage(text = suggestion)
                                            )
                                        }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = suggestion,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Messages List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(state.messages) { message ->
                        MessageBubbleRow(message = message, isDark = isDark)
                    }
                    
                    if (state.isLoading) {
                        item {
                            AiTypingIndicator(isDark = isDark)
                        }
                    }
                }
            }

            // ChatGPT-style Chat input bar
            ChatInputFooter(
                isDark = isDark,
                onAttachmentClick = { imagePickerLauncher.launch("image/*") },
                onSendMessage = { text ->
                    viewModel.sendIntent(
                        AiChatContract.Intent.SendMessage(text = text)
                    )
                }
            )
        }
    }
}

@Composable
fun AuthRequiredScreen(
    onBackClick: () -> Unit,
    onAuthClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AiChatHeader(
                onBackClick = onBackClick,
                onResetClick = {}
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Authentication Required",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Please sign in to access Eslam, your AI shopping assistant, sync your chat history, and get personalized recommendations.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onAuthClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6F32E5)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sign In to Continue",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AiTypingIndicator(
    isDark: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(end = 8.dp, top = 4.dp)
                .size(28.dp)
                .background(
                    brush = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF4F46E5))),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_ai),
                contentDescription = "AI",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Box(
            modifier = Modifier
                .background(
                    if (isDark) Color(0xFF242A31) else Color(0xFFF3F4F6),
                    shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val transition = rememberInfiniteTransition(label = "dots")
                
                val dot1Alpha by transition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot1"
                )
                
                val dot2Alpha by transition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = 200, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot2"
                )
                
                val dot3Alpha by transition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = 400, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot3"
                )

                val dotColor = if (isDark) Color(0xFF9CA3AF) else Color(0xFF4B5563)

                Box(modifier = Modifier.size(6.dp).background(dotColor.copy(alpha = dot1Alpha), CircleShape))
                Box(modifier = Modifier.size(6.dp).background(dotColor.copy(alpha = dot2Alpha), CircleShape))
                Box(modifier = Modifier.size(6.dp).background(dotColor.copy(alpha = dot3Alpha), CircleShape))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatHeader(
    onBackClick: () -> Unit,
    onResetClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "AI Assistant",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onResetClick) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Reset Chat"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun MessageBubbleRow(
    message: ChatMessageUi,
    isDark: Boolean
) {
    val isUser = message.sender == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp, top = 4.dp)
                    .size(28.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF4F46E5))),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_ai),
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            if (!message.attachedImageUrl.isNullOrBlank()) {
                UploadedImagePreview(imageUrl = message.attachedImageUrl)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (!message.voiceDuration.isNullOrBlank()) {
                VoiceWaveformVisualizer(isDark = isDark)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (message.text.isNotBlank()) {
                TextBubble(text = message.text, isUser = isUser, isDark = isDark)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (message.recommendedProducts.isNotEmpty()) {
                if (message.recommendedProducts.size == 1) {
                    SingleProductCard(product = message.recommendedProducts.first(), isDark = isDark)
                } else {
                    ProductSuggestionsCard(products = message.recommendedProducts, isDark = isDark)
                }
            }
        }
    }
}

@Composable
fun TextBubble(
    text: String,
    isUser: Boolean,
    isDark: Boolean
) {
    val bubbleColor = if (isUser) {
        if (isDark) Color(0xFF3B1E78) else Color(0xFFE8DDFF)
    } else {
        if (isDark) Color(0xFF242A31) else Color(0xFFF3F4F6)
    }
    
    val textColor = if (isUser) {
        if (isDark) Color(0xFFE5D5FF) else Color(0xFF4A148C)
    } else {
        MaterialTheme.colorScheme.onBackground
    }
    
    val shape = if (isUser) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }
    
    Box(
        modifier = Modifier
            .background(bubbleColor, shape = shape)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = textColor,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun SingleProductCard(
    product: ChatProductUi,
    isDark: Boolean
) {
    val cardBg = if (isDark) Color(0xFF1E242B) else Color.White
    val borderColor = if (isDark) Color(0xFF2E3844) else Color(0xFFE5E7EB)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomNetworkImage(
                    imageUrl = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = product.details,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.price,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "  •  ",
                            fontSize = 14.sp,
                            color = Color.LightGray
                        )
                        Text(
                            text = product.stockStatus,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (product.stockStatus.contains("In Stock")) Color(0xFF2E7D32) else Color(0xFFD84315)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = { /* product navigation */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(listOf(borderColor, borderColor))
                )
            ) {
                Text(
                    text = "View Product",
                    color = if (isDark) Color.White else Color(0xFF6F32E5),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ProductSuggestionsCard(
    products: List<ChatProductUi>,
    isDark: Boolean
) {
    val cardBg = if (isDark) Color(0xFF1E242B) else Color.White
    val borderColor = if (isDark) Color(0xFF2E3844) else Color(0xFFE5E7EB)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column {
            products.forEachIndexed { index, product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* product navigation */ }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomNetworkImage(
                        imageUrl = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = product.price,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "  •  ",
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                            Text(
                                text = product.stockStatus,
                                fontSize = 11.sp,
                                color = if (product.stockStatus.contains("In Stock")) Color(0xFF2E7D32) else Color(0xFFD84315)
                            )
                        }
                    }
                }
                
                if (index < products.lastIndex) {
                    Divider(color = borderColor)
                }
            }
        }
    }
}

@Composable
fun UploadedImagePreview(
    imageUrl: String
) {
    Card(
        modifier = Modifier
            .size(160.dp)
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        CustomNetworkImage(
            imageUrl = imageUrl,
            contentDescription = "Uploaded Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun VoiceWaveformVisualizer(
    isDark: Boolean
) {
    val bubbleColor = if (isDark) Color(0xFF3B1E78) else Color(0xFFE8DDFF)
    
    Box(
        modifier = Modifier
            .background(bubbleColor, shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .width(200.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice search",
                tint = Color(0xFF6F32E5),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val heights = listOf(12, 18, 14, 28, 8, 22, 16, 26, 12, 20, 10, 18)
                heights.forEach { h ->
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(h.dp)
                            .background(Color(0xFF6F32E5), shape = RoundedCornerShape(1.dp))
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputFooter(
    isDark: Boolean,
    onAttachmentClick: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    val containerBg = if (isDark) Color(0xFF1E242B) else Color(0xFFF9FAFB)
    val borderColor = if (isDark) Color(0xFF2E3844) else Color(0xFFE5E7EB)
    var inputText by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Divider(color = borderColor)
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                    .background(containerBg, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Ask anything...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    maxLines = 4,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onAttachmentClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add attachment",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFE8DDFF), shape = CircleShape)
                                .clickable { /* Simulation of voice recorder search trigger */
                                    onSendMessage("Is the linen shirt in size M available?")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice search",
                                tint = Color(0xFF6F32E5),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    onSendMessage(inputText)
                                    inputText = ""
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (inputText.isNotBlank()) Color(0xFF6F32E5) else Color.Transparent,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send message",
                                tint = if (inputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
