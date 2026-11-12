package com.fridgepet.app.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ParseReceiptRequest(
    val imageBase64: String,
    val mimeType: String = "image/jpeg",
    val scanMode: String = "receipt",
)

@Serializable
data class ExtractedItemDto(
    val cleanName: String,
    val rawLabel: String,
    val quantityLabel: String,
    val shelf: String,
    val shelfLifeDays: Int,
    val priceAed: Double,
    val storageTip: String,
)

@Serializable
data class ParseReceiptResponse(
    val merchantName: String,
    val currency: String = "AED",
    val items: List<ExtractedItemDto>,
    val safetyNote: String = "AI expiry dates are estimates — always smell, inspect, and check before eating.",
    val isDemoFallback: Boolean = false,
)

@Serializable
data class ExpiringIngredientDto(
    val name: String,
    val daysUntilExpiry: Int,
    val shelf: String,
    val quantityLabel: String,
)

@Serializable
data class RescueRecipesRequest(
    val petName: String,
    val petState: String,
    val expiringItems: List<ExpiringIngredientDto>,
)

@Serializable
data class RescueRecipeDto(
    val title: String,
    val prepMinutes: Int,
    val usedIngredients: List<String>,
    val extraStaples: List<String>,
    val steps: List<String>,
    val petCheerLine: String,
)

@Serializable
data class RescueRecipesResponse(
    val recipes: List<RescueRecipeDto>,
    val isDemoFallback: Boolean = false,
)

@Serializable
data class PetDialogueRequest(
    val petName: String,
    val petState: String,
    val evolutionStage: Int,
    val expiringSoonNames: List<String>,
    val expiredCount: Int,
    val totalItems: Int,
    val savedAedThisWeek: Double,
)

@Serializable
data class PetDialogueResponse(
    val line: String,
)
