package com.iti.data.repositories

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.firestore.FirebaseFirestore
import com.iti.data.BuildConfig
import com.iti.data.utils.toFriendlyError
import com.iti.domain.models.ChatMessage
import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.domain.repositories.ai.ChatbotRepository
import com.iti.domain.repositories.products.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class ChatbotRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val productsRepository: ProductsRepository,
    private val context: Context
) : ChatbotRepository {

    override fun getChatHistory(userId: String): Flow<Result<List<ChatMessage>>> = callbackFlow {
        trySend(Result.Loading)

        val collectionRef = firestore.collection("users").document(userId).collection("chats")
        val queryRef = collectionRef.orderBy("timestamp")

        val listener = queryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("ChatbotRepository", "Error in getChatHistory listener", error)
                trySend(Result.Failure(error))
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    val resId = context.resources.getIdentifier("ai_greeting_message", "string", context.packageName)
                    val localizedGreeting = if (resId != 0) context.getString(resId) else "Hello! I am Eslam, your personal shopping assistant. I am here to help you choose the best products from our shop."
                    
                    val greetingMsg = ChatMessage(
                        sender = "ai",
                        text = localizedGreeting,
                        timestamp = System.currentTimeMillis()
                    )
                    collectionRef.document("greeting_message").set(greetingMsg)
                    return@addSnapshotListener
                }
                val messages = snapshot.documents.mapNotNull { doc ->
                    val sender = doc.getString("sender") ?: ""
                    val text = doc.getString("text") ?: ""
                    val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                    val recommendedProductIds = (doc.get("recommendedProductIds") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                    val voiceDuration = doc.getString("voiceDuration")
                    val attachedImageUrl = doc.getString("attachedImageUrl")
                    ChatMessage(
                        id = doc.id,
                        sender = sender,
                        text = text,
                        timestamp = timestamp,
                        recommendedProductIds = recommendedProductIds,
                        voiceDuration = voiceDuration,
                        attachedImageUrl = attachedImageUrl
                    )
                }
                trySend(Result.Success(messages))
            }
        }
        awaitClose { listener.remove() }
    }

    override fun sendMessage(
        userId: String,
        userMessage: ChatMessage,
        imageBytes: ByteArray?,
        currencyContext: String?
    ): Flow<Result<ChatMessage>> = flow {
        emit(Result.Loading)
        
        try {
            firestore.collection("users")
                .document(userId)
                .collection("chats")
                .add(userMessage)
                .await()

            var productsList = emptyList<Product>()
            try {
                productsRepository.getProductsByNumber(100).collect { res ->
                    if (res is Result.Success) {
                        productsList = res.data
                    }
                }
            } catch (e: Exception) {
                // ignore
            }

            val catalogText = if (productsList.isNotEmpty()) {
                productsList.joinToString("\n") { prod ->
                    val price = prod.minPrice.amount
                    val currency = prod.minPrice.currencyCode
                    val isAvailable = if (prod.variants.any { it.availableForSale }) "In Stock" else "Out of Stock"
                    "- ID: ${prod.id}, Name: ${prod.title}, Price: $price $currency, Stock: $isAvailable, Details: Vendor=${prod.vendor}, Type=${prod.productType}"
                }
            } else {
                ChatbotSystemPrompt.NO_PRODUCTS_TEXT
            }

            val systemInstructionText = ChatbotSystemPrompt.getSystemPrompt(catalogText) + 
                if (currencyContext != null) "\n\nCRITICAL RULE: The user has selected their currency as $currencyContext. You MUST use this currency when mentioning prices. For example, if the catalog says 'Price: 100 USD', and currency is EGP, DO NOT say USD. State the price strictly in $currencyContext by multiplying the USD price by the exchange rate if you know it, OR just state the price if you don't know the exact rate but emphasize it is in $currencyContext." else ""

            val historySnapshot = firestore.collection("users")
                .document(userId)
                .collection("chats")
                .orderBy("timestamp")
                .limitToLast(10)
                .get()
                .await()

            val historyList = mutableListOf<com.google.ai.client.generativeai.type.Content>()
            
            historySnapshot.documents.forEach { doc ->
                val sender = doc.getString("sender") ?: ""
                val text = doc.getString("text") ?: ""
                val timestamp = doc.getLong("timestamp") ?: 0L
                val recommendedProductIds = (doc.get("recommendedProductIds") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                
                if (timestamp >= userMessage.timestamp) return@forEach
                
                if (sender == "user" || sender == "ai") {
                    val role = if (sender == "user") "user" else "model"
                    
                    val contentText = if (sender == "ai" && recommendedProductIds.isNotEmpty()) {
                        // Enrich AI message with the products it recommended so Gemini remembers the context
                        val recommendedDetails = recommendedProductIds.mapNotNull { id ->
                            productsList.find { it.id == id }?.let { prod ->
                                "${prod.title} (${prod.minPrice.amount} ${prod.minPrice.currencyCode})"
                            }
                        }
                        ChatbotSystemPrompt.getRecommendedProductsSnippet(text, recommendedDetails)
                    } else {
                        text
                    }
                    
                    historyList.add(
                        content(role) {
                            text(contentText)
                        }
                    )
                }
            }

            val model = GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = BuildConfig.GEMINI_API_KEY,
                systemInstruction = content { text(systemInstructionText) }
            )

            val response = if (imageBytes != null) {
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    ?: throw Exception("image") // Throws 'image' so ExceptionHandler maps it to ERROR_IMAGE
                
                val currentContent = content("user") {
                    image(bitmap)
                    val imgDescId = context.resources.getIdentifier("ai_image_description", "string", context.packageName)
                    val imgDescStr = if (imgDescId != 0) context.getString(imgDescId) else "Search by this image."
                    text(userMessage.text.ifBlank { imgDescStr })
                }
                model.generateContent(*(historyList + currentContent).toTypedArray())
            } else {
                val currentContent = content("user") { text(userMessage.text) }
                model.generateContent(*(historyList + currentContent).toTypedArray())
            }

            val responseText = response.text ?: "I am sorry, I couldn't process that."

            val recommendedIds = mutableListOf<String>()
            var cleanResponseText = responseText
            val regex = "\\[RECOMMENDED_PRODUCT_IDS:\\s*(.*?)\\]".toRegex(RegexOption.IGNORE_CASE)
            val matchResult = regex.find(responseText)
            if (matchResult != null) {
                val idsString = matchResult.groupValues[1]
                idsString.split(",").map { it.trim() }.forEach { id ->
                    if (id.isNotBlank()) {
                        recommendedIds.add(id)
                    }
                }
                cleanResponseText = responseText.replace(regex, "").trim()
            }

            val aiMessage = ChatMessage(
                sender = "ai",
                text = cleanResponseText,
                timestamp = System.currentTimeMillis(),
                recommendedProductIds = recommendedIds
            )

            val addedDoc = firestore.collection("users")
                .document(userId)
                .collection("chats")
                .add(aiMessage)
                .await()

            emit(Result.Success(aiMessage.copy(id = addedDoc.id)))
        } catch (e: Exception) {
            Log.e("ChatbotRepository", "Error in sendMessage", e)
            
            val friendlyMsg = e.toFriendlyError()
            
            val errorMsg = ChatMessage(
                sender = "ai",
                text = friendlyMsg,
                timestamp = System.currentTimeMillis()
            )
            val addedDoc = firestore.collection("users")
                .document(userId)
                .collection("chats")
                .add(errorMsg)
                .await()

            emit(Result.Success(errorMsg.copy(id = addedDoc.id)))
        }
    }.flowOn(Dispatchers.IO)

    override fun clearChatHistory(userId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val batch = firestore.batch()
            val querySnapshot = firestore.collection("users")
                .document(userId)
                .collection("chats")
                .get()
                .await()
            
            for (document in querySnapshot.documents) {
                batch.delete(document.reference)
            }
            batch.commit().await()
            
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }.flowOn(Dispatchers.IO)

}
