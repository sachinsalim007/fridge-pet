package com.fridgepet.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fridgepet.app.domain.ShelfType

enum class ItemStatus {
    ACTIVE,
    USED,
    WASTED,
}

@Entity(tableName = "food_items")
data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val rawLabel: String = "",
    val quantityLabel: String = "1 unit",
    val shelf: ShelfType = ShelfType.FRIDGE,
    val expiryEpochDay: Long,
    val priceAed: Double = 0.0,
    val storageTip: String = "",
    val status: ItemStatus = ItemStatus.ACTIVE,
    val addedAtEpochMs: Long = System.currentTimeMillis(),
    val resolvedAtEpochMs: Long? = null,
)

@Entity(tableName = "pet_profile")
data class PetProfileEntity(
    @PrimaryKey val id: Int = 1,
    val speciesId: String = "MOCHI",
    val name: String = "Mochi",
    val onboarded: Boolean = false,
    val notificationHour: Int = 9,
    val consecutiveGoodWeeks: Int = 0,
    val celebratingUntilEpochMs: Long = 0L,
    val lastDialogue: String = "Scan a receipt so we can keep every dirham fresh!",
    val lastDialogueUpdatedMs: Long = 0L,
)

@Entity(tableName = "waste_events")
data class WasteEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemName: String,
    val priceAed: Double,
    val estimatedGrams: Int = 350,
    val rescued: Boolean,
    val weekStartEpochDay: Long,
    val timestampEpochMs: Long = System.currentTimeMillis(),
)
