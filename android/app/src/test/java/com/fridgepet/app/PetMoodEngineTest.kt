package com.fridgepet.app

import com.fridgepet.app.domain.EvolutionStage
import com.fridgepet.app.domain.PetMoodEngine
import com.fridgepet.app.domain.PetSpecies
import com.fridgepet.app.domain.PetState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class PetMoodEngineTest {

    private val today = LocalDate.of(2026, 11, 11)
    private val noon = LocalTime.of(12, 0)
    private val lateNight = LocalTime.of(23, 30)

    @Test
    fun `returns HUNGRY when fewer than 3 items exist`() {
        val mood = PetMoodEngine.computeMood(
            expiryDatesEpochDay = listOf(today.plusDays(5).toEpochDay()),
            consecutiveGoodWeeks = 0,
            celebratingUntilEpochMs = 0L,
            nowEpochMs = 1000L,
            today = today,
            localTime = noon,
        )
        assertEquals(PetState.HUNGRY, mood.state)
        assertFalse(mood.hasCozyScarf)
    }

    @Test
    fun `unlocks Cozy Scarf after 1 low-waste week`() {
        val mood = PetMoodEngine.computeMood(
            expiryDatesEpochDay = listOf(
                today.plusDays(3).toEpochDay(),
                today.plusDays(4).toEpochDay(),
                today.plusDays(5).toEpochDay(),
            ),
            consecutiveGoodWeeks = 1,
            celebratingUntilEpochMs = 0L,
            nowEpochMs = 1000L,
            today = today,
            localTime = noon,
        )
        assertEquals(PetState.CONTENT, mood.state)
        assertTrue(mood.hasCozyScarf)
        assertEquals(EvolutionStage.STAGE_1, mood.evolutionStage)
    }

    @Test
    fun `returns ANXIOUS when item expires within 2 days`() {
        val mood = PetMoodEngine.computeMood(
            expiryDatesEpochDay = listOf(
                today.plusDays(1).toEpochDay(),
                today.plusDays(5).toEpochDay(),
                today.plusDays(6).toEpochDay(),
            ),
            consecutiveGoodWeeks = 0,
            celebratingUntilEpochMs = 0L,
            nowEpochMs = 1000L,
            today = today,
            localTime = noon,
        )
        assertEquals(PetState.ANXIOUS, mood.state)
        assertEquals(1, mood.expiringSoonCount)
    }

    @Test
    fun `returns SICK when expired item is sitting on shelf`() {
        val mood = PetMoodEngine.computeMood(
            expiryDatesEpochDay = listOf(
                today.minusDays(1).toEpochDay(),
                today.plusDays(5).toEpochDay(),
            ),
            consecutiveGoodWeeks = 1,
            celebratingUntilEpochMs = 0L,
            nowEpochMs = 1000L,
            today = today,
            localTime = lateNight,
        )
        assertEquals(PetState.SICK, mood.state)
        assertEquals(1, mood.expiredCount)
    }

    @Test
    fun `returns SLEEPY at night when no expired food`() {
        val mood = PetMoodEngine.computeMood(
            expiryDatesEpochDay = listOf(
                today.plusDays(4).toEpochDay(),
                today.plusDays(5).toEpochDay(),
                today.plusDays(6).toEpochDay(),
            ),
            consecutiveGoodWeeks = 2,
            celebratingUntilEpochMs = 0L,
            nowEpochMs = 1000L,
            today = today,
            localTime = lateNight,
        )
        assertEquals(PetState.SLEEPY, mood.state)
        assertEquals(EvolutionStage.STAGE_2, mood.evolutionStage)
    }

    @Test
    fun `verifies 10 PetSpecies entries exist with unique affinities`() {
        assertEquals(10, PetSpecies.entries.size)
        val species = PetSpecies.fromId("ZAATAR")
        assertEquals("Zaatar", species.displayName)
        assertEquals("Bakery & Flatbreads", species.foodAffinity)
    }
}
