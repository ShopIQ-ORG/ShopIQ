package com.iti.data.repositories

import android.util.Base64
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
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
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// Request JSON Mapping
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiSystemInstruction? = null
)

data class GeminiContent(
    val role: String,
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String? = null,
    val inlineData: GeminiInlineData? = null
)

data class GeminiInlineData(
    val mimeType: String,
    val data: String
)

data class GeminiSystemInstruction(
    val parts: List<GeminiPart>
)

// Response JSON Mapping
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

data class GeminiCandidate(
    val content: GeminiContent? = null
)

class ChatbotRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val productsRepository: ProductsRepository,
    private val gson: Gson
) : ChatbotRepository {

    override fun getChatHistory(userId: String): Flow<Result<List<ChatMessage>>> = callbackFlow {
        val ref = firestore.collection("users").document(userId).collection("chats")
            .orderBy("timestamp")

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("ChatbotRepository", "Error in getChatHistory listener", error)
                trySend(Result.Failure(error))
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val messages = snapshot.documents.mapNotNull { doc ->
                    val sender = doc.getString("sender") ?: ""
                    val text = doc.getString("text") ?: ""
                    val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                    val recommendedProductIds = doc.get("recommendedProductIds") as? List<String> ?: emptyList()
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
            // 1. Add user message to Firestore
            firestore.collection("users")
                .document(userId)
                .collection("chats")
                .add(userMessage)
                .await()

            // 2. Fetch products to get the store catalog context
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

            // 3. Format product catalog
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

            // 4. Build prompt and system instructions
            val systemInstructionText = """
                You are Eslam, a friendly and professional personal shopping assistant for the ShopIQ store.
                Greet the user warmly in English: "Hello! I am Eslam, your personal shopping assistant. I am here to help you choose the best products from our shop."
                
                You have access to the store's product catalog. Refer to these products when answering:
                $catalogText
                
                Rules:
                1. Answer the user's queries in English. Be polite and helpful.
                2. If the user asks about product availability, features, sizes, colors, or prices, search the catalog.
                3. If a product is in the catalog, tell them it is available.
                4. If a product is not in the catalog, tell them we do not have it, but suggest the closest alternatives.
                5. If you recommend or refer to any products from the catalog, you MUST output their exact product IDs at the very end of your response in this exact format:
                [RECOMMENDED_PRODUCT_IDS: id1, id2, id3]
                Only list IDs that are exactly in the catalog. If no products are recommended, do not include this tag.
            """.trimIndent()

            // 5. Fetch past chat history for context (limit to last 10)
            val historySnapshot = firestore.collection("users")
                .document(userId)
                .collection("chats")
                .orderBy("timestamp")
                .limitToLast(10)
                .get()
                .await()

            // 6. Map history to Gemini API format
            val contents = mutableListOf<GeminiContent>()
            
            historySnapshot.documents.forEach { doc ->
                val sender = doc.getString("sender") ?: ""
                val text = doc.getString("text") ?: ""
                if (sender == "user" || sender == "ai") {
                    val role = if (sender == "user") "user" else "model"
                    contents.add(
                        GeminiContent(
                            role = role,
                            parts = listOf(GeminiPart(text = text))
                        )
                    )
                }
            }

            // Ensure the latest user message is added to contents (with image attachment if present)
            val lastMsgInHistory = contents.lastOrNull()
            if (lastMsgInHistory == null || lastMsgInHistory.parts.firstOrNull()?.text != userMessage.text) {
                val partsList = mutableListOf<GeminiPart>()
                partsList.add(GeminiPart(text = userMessage.text))
                if (imageBytes != null) {
                    val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    partsList.add(
                        GeminiPart(
                            inlineData = GeminiInlineData(
                                mimeType = "image/jpeg",
                                data = base64Image
                            )
                        )
                    )
                }
                contents.add(GeminiContent(role = "user", parts = partsList))
            }

            // 7. Construct Request Body JSON
            val requestObject = GeminiRequest(
                contents = contents,
                systemInstruction = GeminiSystemInstruction(
                    parts = listOf(GeminiPart(text = systemInstructionText))
                )
            )
            val requestBodyJson = gson.toJson(requestObject)

            // 8. Execute OkHttp call directly to Gemini REST endpoint
            val okHttpClient = OkHttpClient()
            val requestBody =
                requestBodyJson
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            
            Log.d("ChatbotRepository", "Using API Key prefix: ${BuildConfig.GEMINI_API_KEY.take(5)}... (Length: ${BuildConfig.GEMINI_API_KEY.length})")
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${BuildConfig.GEMINI_API_KEY}"
            
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: ""
                throw Exception("Gemini API Error (Code ${response.code}): $errorBody")
            }

            val responseBodyJson = response.body?.string() ?: throw Exception("Empty response body from Gemini")
            val geminiResponse = gson.fromJson(responseBodyJson, GeminiResponse::class.java)
            
            val responseText = geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("No text response generated by Gemini")

            // 9. Parse recommended product IDs from response
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

            // 10. Create and save AI response message
            val aiMessage = ChatMessage(
                sender = "ai",
                text = cleanResponseText,
                timestamp = System.currentTimeMillis(),
                recommendedProductIds = recommendedIds
            )

            firestore.collection("users")
                .document(userId)
                .collection("chats")
                .add(aiMessage)
                .await()

            emit(Result.Success(aiMessage))
        } catch (e: Exception) {
            Log.e("ChatbotRepository", "Error in sendMessage (OkHttp)", e)
            emit(Result.Failure(e))
        }
    }.flowOn(Dispatchers.IO)
    }

