package com.fridgepet.app.domain

import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

enum class PetSpecies(
    val id: String,
    val displayName: String,
    val title: String,
    val foodAffinity: String,
    val primaryColorHex: Long,
    val accentColorHex: Long,
    val description: String,
) {
    MOCHI(
        id = "MOCHI",
        displayName = "Mochi",
        title = "Mint Jelly Blob",
        foodAffinity = "Fresh Greens & Dairy",
        primaryColorHex = 0xFF7AD7BE,
        accentColorHex = 0xFF1F6E58,
        description = "A squishy, playful creature that loves cold crisper greens.",
    ),
    ZAATAR(
        id = "ZAATAR",
        displayName = "Zaatar",
        title = "Sesame Puff",
        foodAffinity = "Bakery & Flatbreads",
        primaryColorHex = 0xFFE0BB72,
        accentColorHex = 0xFF8A5A18,
        description = "Toasty and fragrant, guards Arabic breads and dry grains.",
    ),
    CHILLI(
        id = "CHILLI",
        displayName = "Chilli",
        title = "Spicy Pepper",
        foodAffinity = "Spices, Sauces & Curries",
        primaryColorHex = 0xFFE06655,
        accentColorHex = 0xFF8E1D13,
        description = "Feisty and warm, gets fiery when leftovers sit too long.",
    ),
    FROSTY(
        id = "FROSTY",
        displayName = "Frosty",
        title = "Ice Sprite",
        foodAffinity = "Freezer Goods",
        primaryColorHex = 0xFF99D5E8,
        accentColorHex = 0xFF236882,
        description = "Chilly and sparkling, thrives when groceries get frozen.",
    ),
    LABAN(
        id = "LABAN",
        displayName = "Laban",
        title = "Cream Droplet",
        foodAffinity = "Milk, Yogurt & Labneh",
        primaryColorHex = 0xFFEAEFF0,
        accentColorHex = 0xFF4A7C8C,
        description = "A smooth swirling droplet watching over fresh dairy.",
    ),
    BERRY(
        id = "BERRY",
        displayName = "Berry",
        title = "Berry Puff",
        foodAffinity = "Fruits & Berries",
        primaryColorHex = 0xFFD885B8,
        accentColorHex = 0xFF7A245C,
        description = "Bouncy and sweet, keeps berries from turning sad and moldy.",
    ),
    OLIVE(
        id = "OLIVE",
        displayName = "Olive",
        title = "Emerald Pip",
        foodAffinity = "Oils, Pickles & Mezze",
        primaryColorHex = 0xFF9AB865,
        accentColorHex = 0xFF3D5915,
        description = "Sleek and wise, keeps pantry jars and olive oils pristine.",
    ),
    GINGER(
        id = "GINGER",
        displayName = "Ginger",
        title = "Root Buddy",
        foodAffinity = "Roots, Garlic & Teas",
        primaryColorHex = 0xFFD4A06B,
        accentColorHex = 0xFF7A4A1C,
        description = "Knobby and lively, protects ginger, garlic, and fresh herbs.",
    ),
    SPUD(
        id = "SPUD",
        displayName = "Spud",
        title = "Freckled Potato",
        foodAffinity = "Potatoes, Onions & Staples",
        primaryColorHex = 0xFFC9A882,
        accentColorHex = 0xFF5C4022,
        description = "Cozy and grounded, rests comfortably in dark dry pantry bins.",
    ),
    CITRUS(
        id = "CITRUS",
        displayName = "Citrus",
        title = "Sun Wedge",
        foodAffinity = "Citrus & Juices",
        primaryColorHex = 0xFFF5B738,
        accentColorHex = 0xFF945E00,
        description = "Zesty and radiant, spreads sunshine over fruit baskets.",
    );

    companion object {
        fun fromId(id: String): PetSpecies =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MOCHI
    }
}

enum class PetState(
    val label: String,
    val subtitle: String,
) {
    CONTENT("Content", "Your pantry is balanced and cozy."),
    HUNGRY("Hungry", "The fridge is nearly empty — time for a grocery run!"),
    ANXIOUS("Anxious", "Items are expiring within 2 days. Let's rescue them!"),
    SICK("Feeling Sick", "Expired food is still sitting on a shelf."),
    CELEBRATING("Celebrating", "Zero-waste streak! Food and dirhams saved."),
    SLEEPY("Sleepy", "Night mode — resting quietly inside the crisper."),
}

enum class EvolutionStage(
    val stageNumber: Int,
    val title: String,
    val requiredGoodWeeks: Int,
    val description: String,
) {
    STAGE_1(1, "Frosty Sprout", 0, "A curious little fridge creature just hatched."),
    STAGE_2(2, "Pantry Puffle", 2, "Unlocked after 2 low-waste weeks — sprouts a leafy antenna!"),
    STAGE_3(3, "Chef Guardian", 4, "Unlocked after 4 low-waste weeks — wears a golden chef toque!"),
    ;

    companion object {
        fun fromGoodWeeks(consecutiveGoodWeeks: Int): EvolutionStage = when {
            consecutiveGoodWeeks >= STAGE_3.requiredGoodWeeks -> STAGE_3
            consecutiveGoodWeeks >= STAGE_2.requiredGoodWeeks -> STAGE_2
            else -> STAGE_1
        }
    }
}

enum class ShelfType(val displayName: String) {
    FRIDGE("Fridge"),
    FREEZER("Freezer"),
    PANTRY("Pantry"),
}

data class MoodSnapshot(
    val state: PetState,
    val evolutionStage: EvolutionStage,
    val expiredCount: Int,
    val expiringSoonCount: Int,
    val activeItemCount: Int,
    val hasCozyScarf: Boolean = false,
)

object PetMoodEngine {

    fun computeMood(
        expiryDatesEpochDay: List<Long>,
        consecutiveGoodWeeks: Int,
        celebratingUntilEpochMs: Long,
        nowEpochMs: Long = System.currentTimeMillis(),
        today: LocalDate = LocalDate.now(),
        localTime: LocalTime = LocalTime.now(),
    ): MoodSnapshot {
        val todayDay = today.toEpochDay()
        var expiredCount = 0
        var expiringSoonCount = 0

        for (expiryDay in expiryDatesEpochDay) {
            val delta = expiryDay - todayDay
            when {
                delta < 0 -> expiredCount++
                delta <= 2 -> expiringSoonCount++
            }
        }

        val activeCount = expiryDatesEpochDay.size
        val isNight = localTime.hour >= 22 || localTime.hour < 6

        val state = when {
            celebratingUntilEpochMs > nowEpochMs -> PetState.CELEBRATING
            expiredCount > 0 -> PetState.SICK
            isNight -> PetState.SLEEPY
            expiringSoonCount > 0 -> PetState.ANXIOUS
            activeCount <= 2 -> PetState.HUNGRY
            else -> PetState.CONTENT
        }

        return MoodSnapshot(
            state = state,
            evolutionStage = EvolutionStage.fromGoodWeeks(consecutiveGoodWeeks),
            expiredCount = expiredCount,
            expiringSoonCount = expiringSoonCount,
            activeItemCount = activeCount,
            hasCozyScarf = consecutiveGoodWeeks >= 1,
        )
    }

    fun daysUntilExpiry(expiryEpochDay: Long, today: LocalDate = LocalDate.now()): Int {
        return ChronoUnit.DAYS.between(today, LocalDate.ofEpochDay(expiryEpochDay)).toInt()
    }
}
