package com.fridgepet.app.domain

import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

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
)

object PetMoodEngine {

    /**
     * Computes the pet's current mood from active inventory, celebration window, and time of day.
     * Priority:
     * 1. CELEBRATING (if celebratingUntilEpochMs is in the future — immediate reward after rescue/zero-waste)
     * 2. SICK (any active item already past its expiry date)
     * 3. SLEEPY (night mode 22:00..05:59 when not sick)
     * 4. ANXIOUS (any active item expiring today..within 2 days)
     * 5. HUNGRY (fewer than 3 active items in the fridge/pantry)
     * 6. CONTENT (everything else)
     */
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
        )
    }

    fun daysUntilExpiry(expiryEpochDay: Long, today: LocalDate = LocalDate.now()): Int {
        return ChronoUnit.DAYS.between(today, LocalDate.ofEpochDay(expiryEpochDay)).toInt()
    }
}
