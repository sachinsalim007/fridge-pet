package com.fridgepet.app.data.remote

import com.fridgepet.app.BuildConfig
import com.fridgepet.app.domain.PetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class FirebaseAiClient(
    private val baseUrl: String = BuildConfig.FRIDGE_PET_API_BASE_URL,
) : FridgeAiService {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    val isRemoteConfigured: Boolean
        get() = baseUrl.isNotBlank()

    override suspend fun parseReceipt(
        imageBase64: String,
        mimeType: String,
        scanMode: String,
    ): ParseReceiptResponse {
        if (!isRemoteConfigured) {
            return DemoFallbackAiService.parseReceipt(imageBase64, mimeType, scanMode)
        }
        return runCatching {
            val payload = json.encodeToString(
                ParseReceiptRequest(
                    imageBase64 = imageBase64,
                    mimeType = mimeType,
                    scanMode = scanMode,
                )
            )
            val responseJson = postJson("parseReceipt", payload)
            json.decodeFromString<ParseReceiptResponse>(responseJson)
        }.getOrElse {
            DemoFallbackAiService.parseReceipt(imageBase64, mimeType, scanMode)
        }
    }

    override suspend fun suggestRescueRecipes(
        petName: String,
        petState: PetState,
        expiringItems: List<ExpiringIngredientDto>,
    ): RescueRecipesResponse {
        if (!isRemoteConfigured) {
            return DemoFallbackAiService.suggestRescueRecipes(petName, petState, expiringItems)
        }
        return runCatching {
            val payload = json.encodeToString(
                RescueRecipesRequest(
                    petName = petName,
                    petState = petState.name,
                    expiringItems = expiringItems,
                )
            )
            val responseJson = postJson("suggestRescueRecipes", payload)
            json.decodeFromString<RescueRecipesResponse>(responseJson)
        }.getOrElse {
            DemoFallbackAiService.suggestRescueRecipes(petName, petState, expiringItems)
        }
    }

    override suspend fun getPetDialogue(request: PetDialogueRequest): String {
        if (!isRemoteConfigured) {
            return DemoFallbackAiService.getPetDialogue(request)
        }
        return runCatching {
            val payload = json.encodeToString(request)
            val responseJson = postJson("getPetDialogue", payload)
            json.decodeFromString<PetDialogueResponse>(responseJson).line
        }.getOrElse {
            DemoFallbackAiService.getPetDialogue(request)
        }
    }

    private suspend fun postJson(endpoint: String, body: String): String =
        withContext(Dispatchers.IO) {
            val normalizedBase = baseUrl.trimEnd('/')
            val request = Request.Builder()
                .url("$normalizedBase/$endpoint")
                .post(body.toRequestBody(jsonMediaType))
                .build()

            httpClient.newCall(request).execute().use { response ->
                val text = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    error("HTTP ${response.code}: $text")
                }
                text
            }
        }
}
