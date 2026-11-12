package com.fridgepet.app.data.remote

import com.fridgepet.app.domain.PetState

interface FridgeAiService {
    suspend fun parseReceipt(
        imageBase64: String,
        mimeType: String,
        scanMode: String,
    ): ParseReceiptResponse

    suspend fun suggestRescueRecipes(
        petName: String,
        petState: PetState,
        expiringItems: List<ExpiringIngredientDto>,
    ): RescueRecipesResponse

    suspend fun getPetDialogue(request: PetDialogueRequest): String
}

/**
 * Local fallback used when Firebase Cloud Functions URL is not configured yet
 * or when testing offline. Mirrors the exact UAE bilingual receipt cleanup & rescue chef flow.
 */
object DemoFallbackAiService : FridgeAiService {

    override suspend fun parseReceipt(
        imageBase64: String,
        mimeType: String,
        scanMode: String,
    ): ParseReceiptResponse {
        val sampleItems = if (scanMode == "fridge") {
            listOf(
                ExtractedItemDto(
                    cleanName = "fresh milk",
                    rawLabel = "Almarai Fresh Milk",
                    quantityLabel = "1 L",
                    shelf = "FRIDGE",
                    shelfLifeDays = 2,
                    priceAed = 7.50,
                    storageTip = "Store on a middle shelf rather than the warmer fridge door.",
                ),
                ExtractedItemDto(
                    cleanName = "baby spinach",
                    rawLabel = "Organic Spinach Bag",
                    quantityLabel = "250 g",
                    shelf = "FRIDGE",
                    shelfLifeDays = 2,
                    priceAed = 12.00,
                    storageTip = "Tuck a dry paper towel inside the bag to absorb excess moisture.",
                ),
                ExtractedItemDto(
                    cleanName = "greek yogurt",
                    rawLabel = "Greek Yogurt 400g",
                    quantityLabel = "400 g",
                    shelf = "FRIDGE",
                    shelfLifeDays = 6,
                    priceAed = 14.50,
                    storageTip = "Keep tightly sealed; use a clean spoon each scoop.",
                ),
            )
        } else {
            listOf(
                ExtractedItemDto(
                    cleanName = "chicken breast",
                    rawLabel = "CHKN BRST 1KG / صدر دجاج",
                    quantityLabel = "1 kg",
                    shelf = "FRIDGE",
                    shelfLifeDays = 2,
                    priceAed = 26.50,
                    storageTip = "Keep on the coldest bottom shelf; freeze portions you will not cook within 2 days.",
                ),
                ExtractedItemDto(
                    cleanName = "fresh milk",
                    rawLabel = "ALMARAI FRSH MLK 1L / حليب طازج",
                    quantityLabel = "1 L",
                    shelf = "FRIDGE",
                    shelfLifeDays = 5,
                    priceAed = 7.25,
                    storageTip = "Store in the main compartment away from the fridge door.",
                ),
                ExtractedItemDto(
                    cleanName = "tomatoes",
                    rawLabel = "طماطم محلي / TOMATO LOC 500G",
                    quantityLabel = "500 g",
                    shelf = "PANTRY",
                    shelfLifeDays = 4,
                    priceAed = 5.50,
                    storageTip = "Keep stem-side down on the counter until ripe, then chill if needed.",
                ),
                ExtractedItemDto(
                    cleanName = "arabic flatbread",
                    rawLabel = "KHBZ ARABI / خبز عربي 6PCS",
                    quantityLabel = "6 pcs",
                    shelf = "PANTRY",
                    shelfLifeDays = 3,
                    priceAed = 3.00,
                    storageTip = "Freeze extra loaves flat in a zip bag and toast straight from frozen.",
                ),
                ExtractedItemDto(
                    cleanName = "labneh",
                    rawLabel = "لبنة تركية / TURK LABNEH 400G",
                    quantityLabel = "400 g",
                    shelf = "FRIDGE",
                    shelfLifeDays = 10,
                    priceAed = 13.75,
                    storageTip = "Smooth the top and drizzle a thin layer of olive oil to keep it fresh longer.",
                ),
            )
        }

        return ParseReceiptResponse(
            merchantName = if (scanMode == "fridge") "Fridge Photo (Demo)" else "Carrefour UAE (Demo)",
            currency = "AED",
            items = sampleItems,
            safetyNote = "AI expiry dates are estimates — always smell, inspect, and check before eating.",
            isDemoFallback = true,
        )
    }

    override suspend fun suggestRescueRecipes(
        petName: String,
        petState: PetState,
        expiringItems: List<ExpiringIngredientDto>,
    ): RescueRecipesResponse {
        val names = expiringItems.map { it.name }
        val primary = names.getOrElse(0) { "fridge greens" }
        val secondary = names.getOrElse(1) { "protein" }
        val tertiary = names.getOrElse(2) { "bread" }

        val recipes = listOf(
            RescueRecipeDto(
                title = "15-Min Spiced ${primary.replaceFirstChar { it.uppercase() }} & ${secondary.replaceFirstChar { it.uppercase() }} Skillet",
                prepMinutes = 15,
                usedIngredients = names.take(3),
                extraStaples = listOf("olive oil", "garlic", "cumin", "salt & pepper"),
                steps = listOf(
                    "Dice $primary and $secondary into bite-sized pieces.",
                    "Heat 1 tbsp olive oil with crushed garlic in a wide pan over medium-high heat.",
                    "Sear $primary for 5–6 minutes until golden, then toss in $secondary and a pinch of cumin.",
                    "Serve hot right away or wrap in warm $tertiary!",
                ),
                petCheerLine = "$petName says: Woo! That rescued your soon-to-expire groceries before they turned sad!",
            ),
            RescueRecipeDto(
                title = "Zero-Waste Pantry Frittata / Bowl",
                prepMinutes = 12,
                usedIngredients = names.take(2),
                extraStaples = listOf("2 eggs or yogurt", "olive oil", "black pepper"),
                steps = listOf(
                    "Chop ${names.take(2).joinToString(" and ")} finely.",
                    "Sauté in olive oil for 4 minutes until tender and fragrant.",
                    "Fold into beaten eggs (or spoon over warm grains/yogurt) and season generously.",
                ),
                petCheerLine = "$petName is doing a happy wiggle — dinner saved in 12 minutes!",
            ),
        )

        return RescueRecipesResponse(recipes = recipes, isDemoFallback = true)
    }

    override suspend fun getPetDialogue(request: PetDialogueRequest): String {
        val firstSoon = request.expiringSoonNames.firstOrNull()
        return when (request.petState) {
            PetState.CELEBRATING.name ->
                "Woohoo! We saved AED ${request.savedAedThisWeek.toInt()} this week — my tummy is glowing!"
            PetState.SICK.name ->
                "Ugh, my tummy hurts... ${request.expiredCount} expired item(s) need clearing out!"
            PetState.SLEEPY.name ->
                "Zzz... keeping the crisper cool tonight. See you at breakfast!"
            PetState.ANXIOUS.name ->
                if (firstSoon != null) {
                    "Psst! Your $firstSoon only has a day or two left — cook or freeze it?"
                } else {
                    "A few items are getting close to expiry — want a rescue recipe?"
                }
            PetState.HUNGRY.name ->
                "Echo... echo... it's roomy in here! Scan a receipt after your next shop."
            else ->
                "Crisp, cool, and cozy! Everything on the shelves is looking fresh."
        }
    }
}
