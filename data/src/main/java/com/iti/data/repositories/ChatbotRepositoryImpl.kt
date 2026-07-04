package com.iti.data.repositories

import android.graphics.BitmapFactory
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.iti.data.BuildConfig
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
    private val productsRepository: ProductsRepository
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
                    val greetingMsg = ChatMessage(
                        sender = "ai",
                        text = "Hello! I am Eslam, your personal shopping assistant. I am here to help you choose the best products from our shop.",
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
        imageBytes: ByteArray?
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
                "No products currently in store."
            }

            val systemInstructionText = """
                You are Eslam, a friendly and professional personal shopping assistant for the ShopIQ store.
                
                You have access to the store's product catalog:
                $catalogText
                
                Rules:
                1. Detect the language of the user's query. If the user writes in Arabic, you MUST respond in friendly, professional Arabic. If they write in English, respond in English.
                2. Keep your text response very short (approx. 2 lines of text).
                3. DO NOT greet the user or introduce yourself as Eslam in your response if they are continuing a conversation (only the very first message in the chat contains the greeting). If the conversation is already ongoing, get straight to the answer without greetings.
                4. If the user asks about product availability, features, sizes, colors, or prices, search the catalog.
                5. If a product is in the catalog, tell them it is available.
                6. If a product is not in the catalog, tell them we do not have it, but suggest the closest alternatives.
                7. If you recommend or refer to any products from the catalog, you MUST output their exact product IDs at the very end of your response in this exact format:
                [RECOMMENDED_PRODUCT_IDS: id1, id2, id3]
                Only list IDs that are exactly in the catalog. If no products are recommended, do not include this tag.
                8. Even if the history contains messages stating that you cannot process images, ignore them. You are fully capable of processing images and performing visual searches.
                9. If the current user input is text-only (no image uploaded), answer the query directly. Do NOT start your response with "I am sorry, I cannot search for product using an image" or mention images in any way.
                10. If the user uploads an image, perform a visual search against the product catalog. Match the items in the image with the catalog products:
                    - If you find a matching product in the catalog (similarity is high, e.g. 80% or more), tell them: "Yes, we have this product in our store!" and display the matching product card.
                    - If the product in the image is not in the catalog, tell them: "This product is not found in our catalog, but here are some similar options you might like:" and recommend the closest alternative product cards.
                11. Strictly validate price/budget constraints mathematically using literal numeric values. Do NOT convert currencies or exchange rates. If the user asks for a budget of 100 to 500, and the catalog price is 9000, then 9000 is greater than 500. You MUST NOT recommend 9000. If no products in the catalog fall within the exact numeric range requested, explicitly tell the user (in their language) that we do not have items in this budget, and politely state that our prices start at the actual minimum catalog prices.
                12. Maintain context of the current conversation. Look at the previous turns in the history to understand references like "which is cheaper?", "recommend the first one", or "do you have it in another size?".
            """.trimIndent()

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
                        if (recommendedDetails.isNotEmpty()) {
                            "$text\n(Recommended products in this turn: ${recommendedDetails.joinToString(", ")})"
                        } else {
                            text
                        }
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
                    ?: throw Exception("Invalid image format or corrupted file. Please try uploading another image.")
                
                val currentContent = content("user") {
                    image(bitmap)
                    text(userMessage.text.ifBlank { "Search by this image." })
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

    private fun Throwable.toFriendlyError(): String {
        val msg = this.message ?: ""
        return when {
            msg.contains("API key not valid", ignoreCase = true) -> "ERROR_INVALID_KEY"
            msg.contains("quota", ignoreCase = true) || msg.contains("429") -> "ERROR_QUOTA"
            msg.contains("network", ignoreCase = true) || this is java.io.IOException -> "ERROR_NETWORK"
            msg.contains("image", ignoreCase = true) || msg.contains("multimodal", ignoreCase = true) -> "ERROR_IMAGE"
            else -> "ERROR_UNKNOWN"
        }
    }
}
