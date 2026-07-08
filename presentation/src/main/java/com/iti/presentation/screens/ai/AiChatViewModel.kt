package com.iti.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.ChatMessage
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.repositories.products.ProductsRepository
import com.iti.domain.usecases.ai.GetChatHistoryUseCase
import com.iti.domain.usecases.ai.SendChatMessageUseCase
import com.iti.presentation.screens.ai.AiChatContract.ChatMessageUi
import com.iti.presentation.screens.ai.AiChatContract.ChatProductUi
import com.iti.presentation.util.CurrencyManager
import com.iti.presentation.util.NetworkMonitor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AiChatViewModel(
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val productsRepository: ProductsRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _state = MutableStateFlow(AiChatContract.State())
    val state: StateFlow<AiChatContract.State> = _state.asStateFlow()

    private val productCache = mutableMapOf<String, ChatProductUi>()
    private var historyJob: Job? = null
    private var currencyObserverJob: Job? = null

    // Observe currency changes and invalidate cache so prices are recalculated
    private fun observeCurrencyChanges(userId: String) {
        currencyObserverJob?.cancel()
        currencyObserverJob = viewModelScope.launch {
            CurrencyManager.selectedCurrency.collect {
                productCache.clear()
                // Re-resolve products with new currency if we have messages
                val currentMessages = _state.value.messages
                if (currentMessages.isNotEmpty()) {
                    observeChatHistory(userId)
                }
            }
        }
    }

    fun sendIntent(intent: AiChatContract.Intent) {
        when (intent) {
            is AiChatContract.Intent.SetUser -> {
                _state.update { it.copy(currentUser = intent.user) }
                if (intent.user is User.AuthenticatedUser) {
                    observeChatHistory(intent.user.uid)
                    observeCurrencyChanges(intent.user.uid)
                } else {
                    historyJob?.cancel()
                    currencyObserverJob?.cancel()
                    productCache.clear()
                    _state.update { it.copy(messages = emptyList()) }
                }
            }
            is AiChatContract.Intent.SendMessage -> {
                val user = _state.value.currentUser
                if (user is User.AuthenticatedUser) {
                    sendMessage(
                        user.uid,
                        intent.text,
                        intent.imageBytes,
                        intent.attachedImageUrl,
                        intent.voiceDuration,
                        com.iti.presentation.util.CurrencyManager.selectedCurrency.value.code
                    )
                }
            }
            is AiChatContract.Intent.ClearChat -> {
                // optional: clear logic
            }
        }
    }

    private fun observeChatHistory(userId: String) {
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            getChatHistoryUseCase(userId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Result.Success -> {
                        _state.update { it.copy(isLoading = false, error = null) }
                        resolveProductsAndEmit(result.data)
                    }
                    is Result.Failure -> {
                        _state.update { it.copy(isLoading = false, error = result.exception.message) }
                    }
                }
            }
        }
    }

    private fun resolveProductsAndEmit(messages: List<ChatMessage>) {
        viewModelScope.launch {
            // Perform background API calls to fetch product details before emitting
            val mappedMessages = messages.map { msg ->
                val productsUi = msg.recommendedProductIds.mapNotNull { id ->
                    if (productCache.containsKey(id)) {
                        productCache[id]
                    } else {
                        var fetchedProd: ChatProductUi? = null
                        try {
                            val longId = id.substringAfterLast("/").toLongOrNull()
                            if (longId != null) {
                                productsRepository.getProductDetails(longId).collect { res ->
                                    if (res is Result.Success) {
                                        val prod = res.data
                                        // Convert price from USD to selected currency
                                        val selectedCurrency = CurrencyManager.selectedCurrency.value
                                        val rawAmountUsd = prod.minPrice.amount.toDoubleOrNull() ?: 0.0
                                        val convertedAmount = CurrencyManager.convertFromUsd(rawAmountUsd)
                                        val formattedAmount = if (convertedAmount % 1.0 == 0.0) {
                                            convertedAmount.toLong().toString()
                                        } else {
                                            String.format("%.2f", convertedAmount)
                                        }
                                        val price = "$formattedAmount ${selectedCurrency.code}"
                                        val img = prod.images.firstOrNull()?.url ?: ""
                                        val stock = if (prod.variants.any { it.availableForSale }) "In Stock" else "Out of Stock"
                                        val ui = ChatProductUi(
                                            id = id,
                                            name = prod.title,
                                            price = price,
                                            imageUrl = img,
                                            stockStatus = stock,
                                            details = prod.vendor
                                        )
                                        productCache[id] = ui
                                        fetchedProd = ui
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            // ignore
                        }
                        fetchedProd
                    }
                }
                ChatMessageUi(
                    id = msg.id,
                    sender = msg.sender,
                    text = msg.text,
                    timestamp = msg.timestamp,
                    recommendedProducts = productsUi,
                    voiceDuration = msg.voiceDuration,
                    attachedImageUrl = msg.attachedImageUrl,
                    isResolvingProducts = false
                )
            }
            _state.update { it.copy(messages = mappedMessages) }
        }
    }

    private fun sendMessage(
        userId: String,
        text: String,
        imageBytes: ByteArray?,
        attachedImageUrl: String?,
        voiceDuration: String?,
        currencyCode: String? = null
    ) {
        viewModelScope.launch {
            if (!networkMonitor.isCurrentlyConnected()) {
                val userMsgUi = ChatMessageUi(
                    id = "temp_user_${System.currentTimeMillis()}",
                    sender = "user",
                    text = text,
                    timestamp = System.currentTimeMillis(),
                    voiceDuration = voiceDuration,
                    attachedImageUrl = attachedImageUrl
                )
                val errorMsgUi = ChatMessageUi(
                    id = "temp_bot_${System.currentTimeMillis()}",
                    sender = "ai",
                    text = "ERROR_NETWORK",
                    timestamp = System.currentTimeMillis() + 10
                )
                _state.update {
                    it.copy(
                        messages = it.messages + userMsgUi + errorMsgUi,
                        isBotTyping = false
                    )
                }
                return@launch
            }

            val userMsg = ChatMessage(
                sender = "user",
                text = text,
                timestamp = System.currentTimeMillis(),
                attachedImageUrl = attachedImageUrl,
                voiceDuration = voiceDuration
            )

            sendChatMessageUseCase(userId, userMsg, imageBytes, currencyCode).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(isBotTyping = true) }
                    }
                    is Result.Success -> {
                        _state.update { it.copy(isBotTyping = false) }
                    }
                    is Result.Failure -> {
                        _state.update { it.copy(isBotTyping = false, error = result.exception.message) }
                    }
                }
            }
        }
    }
}