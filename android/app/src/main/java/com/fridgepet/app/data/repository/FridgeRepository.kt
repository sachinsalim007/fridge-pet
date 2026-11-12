package com.fridgepet.app.data.repository

import com.fridgepet.app.data.local.FoodItemEntity
import com.fridgepet.app.data.local.FridgeDao
import com.fridgepet.app.data.local.ItemStatus
import com.fridgepet.app.data.local.PetProfileEntity
import com.fridgepet.app.data.local.WasteEventEntity
import com.fridgepet.app.data.remote.ExpiringIngredientDto
import com.fridgepet.app.data.remote.FridgeAiService
import com.fridgepet.app.data.remote.ParseReceiptResponse
import com.fridgepet.app.data.remote.PetDialogueRequest
import com.fridgepet.app.data.remote.RescueRecipesResponse
import com.fridgepet.app.domain.MoodSnapshot
import com.fridgepet.app.domain.PetMoodEngine
import com.fridgepet.app.domain.PetState
import com.fridgepet.app.domain.ShelfType
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class EditableScannedItem(
    val localId: String,
    val cleanName: String,
    val rawLabel: String,
    val quantityLabel: String,
    val shelf: ShelfType,
    val shelfLifeDays: Int,
    val priceAed: Double,
    val storageTip: String,
    val selected: Boolean = true,
)

class FridgeRepository(
    private val dao: FridgeDao,
    private val aiService: FridgeAiService,
) {
    fun observeActiveItems(): Flow<List<FoodItemEntity>> = dao.observeActiveItems()

    fun observePetProfile(): Flow<PetProfileEntity?> = dao.observePetProfile()

    fun observeCurrentWeekEvents(): Flow<List<WasteEventEntity>> =
        dao.observeWeeklyEvents(currentWeekStartEpochDay())

    fun observeAllEvents(): Flow<List<WasteEventEntity>> = dao.observeAllEvents()

    suspend fun completeOnboarding(petName: String, seedDemoGroceries: Boolean) {
        val cleanName = petName.trim().ifBlank { "Brrbie" }
        val existing = dao.getPetProfileOnce() ?: PetProfileEntity()
        dao.upsertPetProfile(
            existing.copy(
                id = 1,
                name = cleanName,
                onboarded = true,
                lastDialogue = "Hi! I'm $cleanName. Scan a receipt anytime and I'll watch over your shelves!",
                lastDialogueUpdatedMs = System.currentTimeMillis(),
            )
        )

        if (seedDemoGroceries && dao.getActiveItemsOnce().isEmpty()) {
            val today = LocalDate.now().toEpochDay()
            dao.insertItems(
                listOf(
                    FoodItemEntity(
                        name = "chicken breast",
                        rawLabel = "CHKN BRST 1KG",
                        quantityLabel = "1 kg",
                        shelf = ShelfType.FRIDGE,
                        expiryEpochDay = today + 1,
                        priceAed = 26.50,
                        storageTip = "Keep on the bottom shelf or freeze extra portions today.",
                    ),
                    FoodItemEntity(
                        name = "baby spinach",
                        rawLabel = "ORG SPINACH 250G",
                        quantityLabel = "250 g",
                        shelf = ShelfType.FRIDGE,
                        expiryEpochDay = today + 2,
                        priceAed = 12.00,
                        storageTip = "Slip a dry paper towel into the bag to prevent wilting.",
                    ),
                    FoodItemEntity(
                        name = "fresh milk",
                        rawLabel = "حليب طازج 1L",
                        quantityLabel = "1 L",
                        shelf = ShelfType.FRIDGE,
                        expiryEpochDay = today + 4,
                        priceAed = 7.25,
                        storageTip = "Store on a middle shelf instead of the warm door.",
                    ),
                    FoodItemEntity(
                        name = "arabic flatbread",
                        rawLabel = "خبز عربي",
                        quantityLabel = "6 pcs",
                        shelf = ShelfType.PANTRY,
                        expiryEpochDay = today + 3,
                        priceAed = 3.00,
                        storageTip = "Freeze leftover bread flat and toast directly from frozen.",
                    ),
                )
            )
        }
    }

    suspend fun scanReceiptOrFridge(
        imageBase64: String,
        mimeType: String,
        scanMode: String,
    ): ParseReceiptResponse = aiService.parseReceipt(imageBase64, mimeType, scanMode)

    suspend fun saveReviewedItems(items: List<EditableScannedItem>) {
        val today = LocalDate.now().toEpochDay()
        val entities = items
            .filter { it.selected && it.cleanName.isNotBlank() }
            .map { item ->
                FoodItemEntity(
                    name = item.cleanName.trim(),
                    rawLabel = item.rawLabel.trim(),
                    quantityLabel = item.quantityLabel.trim().ifBlank { "1 unit" },
                    shelf = item.shelf,
                    expiryEpochDay = today + item.shelfLifeDays.coerceIn(1, 365),
                    priceAed = item.priceAed.coerceAtLeast(0.0),
                    storageTip = item.storageTip.trim(),
                    status = ItemStatus.ACTIVE,
                )
            }
        if (entities.isNotEmpty()) {
            dao.insertItems(entities)
            refreshPetDialogue()
        }
    }

    suspend fun markItemUsed(item: FoodItemEntity) {
        val now = System.currentTimeMillis()
        dao.updateItem(
            item.copy(
                status = ItemStatus.USED,
                resolvedAtEpochMs = now,
            )
        )
        dao.insertWasteEvent(
            WasteEventEntity(
                itemName = item.name,
                priceAed = item.priceAed,
                estimatedGrams = 350,
                rescued = true,
                weekStartEpochDay = currentWeekStartEpochDay(),
                timestampEpochMs = now,
            )
        )
        triggerBriefCelebration()
        refreshPetDialogue()
    }

    suspend fun moveToFreezer(item: FoodItemEntity) {
        val today = LocalDate.now().toEpochDay()
        val extendedExpiry = maxOf(item.expiryEpochDay, today) + 30L
        dao.updateItem(
            item.copy(
                shelf = ShelfType.FREEZER,
                expiryEpochDay = extendedExpiry,
                storageTip = "Frozen in time! Keeps well for ~1–3 months; label with today's date.",
            )
        )
        triggerBriefCelebration()
        refreshPetDialogue()
    }

    suspend fun markItemWasted(item: FoodItemEntity) {
        val now = System.currentTimeMillis()
        dao.updateItem(
            item.copy(
                status = ItemStatus.WASTED,
                resolvedAtEpochMs = now,
            )
        )
        dao.insertWasteEvent(
            WasteEventEntity(
                itemName = item.name,
                priceAed = item.priceAed,
                estimatedGrams = 350,
                rescued = false,
                weekStartEpochDay = currentWeekStartEpochDay(),
                timestampEpochMs = now,
            )
        )
        refreshPetDialogue()
    }

    suspend fun markRecipeCooked(usedIngredientNames: List<String>) {
        val active = dao.getActiveItemsOnce()
        val normalizedTargets = usedIngredientNames.map { it.trim().lowercase() }
        val matched = active.filter { entity ->
            val name = entity.name.lowercase()
            normalizedTargets.any { target -> name.contains(target) || target.contains(name) }
        }
        val now = System.currentTimeMillis()
        matched.forEach { entity ->
            dao.updateItem(entity.copy(status = ItemStatus.USED, resolvedAtEpochMs = now))
            dao.insertWasteEvent(
                WasteEventEntity(
                    itemName = entity.name,
                    priceAed = entity.priceAed,
                    estimatedGrams = 350,
                    rescued = true,
                    weekStartEpochDay = currentWeekStartEpochDay(),
                    timestampEpochMs = now,
                )
            )
        }
        triggerBriefCelebration()
        refreshPetDialogue()
    }

    suspend fun suggestRescueRecipes(expiringItems: List<FoodItemEntity>): RescueRecipesResponse {
        val profile = dao.getPetProfileOnce() ?: PetProfileEntity()
        val mood = computeCurrentMood(profile, dao.getActiveItemsOnce())
        val payloadItems = expiringItems.map {
            ExpiringIngredientDto(
                name = it.name,
                daysUntilExpiry = PetMoodEngine.daysUntilExpiry(it.expiryEpochDay),
                shelf = it.shelf.name,
                quantityLabel = it.quantityLabel,
            )
        }
        return aiService.suggestRescueRecipes(
            petName = profile.name,
            petState = mood.state,
            expiringItems = payloadItems,
        )
    }

    suspend fun refreshPetDialogue() {
        val profile = dao.getPetProfileOnce() ?: return
        val active = dao.getActiveItemsOnce()
        val mood = computeCurrentMood(profile, active)
        val soonNames = active
            .filter { PetMoodEngine.daysUntilExpiry(it.expiryEpochDay) in 0..2 }
            .map { it.name }
            .take(5)

        val line = aiService.getPetDialogue(
            PetDialogueRequest(
                petName = profile.name,
                petState = mood.state.name,
                evolutionStage = mood.evolutionStage.stageNumber,
                expiringSoonNames = soonNames,
                expiredCount = mood.expiredCount,
                totalItems = mood.activeItemCount,
                savedAedThisWeek = 0.0,
            )
        )
        dao.upsertPetProfile(
            profile.copy(
                lastDialogue = line,
                lastDialogueUpdatedMs = System.currentTimeMillis(),
            )
        )
    }

    suspend fun recordZeroWasteWeekCelebration() {
        val profile = dao.getPetProfileOnce() ?: PetProfileEntity()
        val nextGoodWeeks = profile.consecutiveGoodWeeks + 1
        dao.upsertPetProfile(
            profile.copy(
                consecutiveGoodWeeks = nextGoodWeeks,
                celebratingUntilEpochMs = System.currentTimeMillis() + 6 * 60 * 60 * 1000L,
            )
        )
        refreshPetDialogue()
    }

    private suspend fun triggerBriefCelebration() {
        val profile = dao.getPetProfileOnce() ?: return
        val remainingActive = dao.getActiveItemsOnce()
        val hasExpired = remainingActive.any { PetMoodEngine.daysUntilExpiry(it.expiryEpochDay) < 0 }
        if (!hasExpired) {
            dao.upsertPetProfile(
                profile.copy(
                    celebratingUntilEpochMs = System.currentTimeMillis() + 45_000L,
                )
            )
        }
    }

    fun computeCurrentMood(
        profile: PetProfileEntity,
        activeItems: List<FoodItemEntity>,
    ): MoodSnapshot {
        return PetMoodEngine.computeMood(
            expiryDatesEpochDay = activeItems.map { it.expiryEpochDay },
            consecutiveGoodWeeks = profile.consecutiveGoodWeeks,
            celebratingUntilEpochMs = profile.celebratingUntilEpochMs,
        )
    }

    companion object {
        fun currentWeekStartEpochDay(today: LocalDate = LocalDate.now()): Long {
            return today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toEpochDay()
        }
    }
}
