package com.fridgepet.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fridgepet.app.data.local.FoodItemEntity
import com.fridgepet.app.data.local.PetProfileEntity
import com.fridgepet.app.data.local.WasteEventEntity
import com.fridgepet.app.data.remote.RescueRecipeDto
import com.fridgepet.app.data.repository.EditableScannedItem
import com.fridgepet.app.data.repository.FridgeRepository
import com.fridgepet.app.domain.EvolutionStage
import com.fridgepet.app.domain.MoodSnapshot
import com.fridgepet.app.domain.PetMoodEngine
import com.fridgepet.app.domain.PetSpecies
import com.fridgepet.app.domain.PetState
import com.fridgepet.app.domain.ShelfType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class FridgeUiState(
    val isLoaded: Boolean = false,
    val petProfile: PetProfileEntity = PetProfileEntity(),
    val species: PetSpecies = PetSpecies.MOCHI,
    val mood: MoodSnapshot = MoodSnapshot(
        state = PetState.HUNGRY,
        evolutionStage = EvolutionStage.STAGE_1,
        expiredCount = 0,
        expiringSoonCount = 0,
        activeItemCount = 0,
        hasCozyScarf = false,
    ),
    val activeItems: List<FoodItemEntity> = emptyList(),
    val needsAttentionItems: List<FoodItemEntity> = emptyList(),
    val weeklyEvents: List<WasteEventEntity> = emptyList(),
    val allEvents: List<WasteEventEntity> = emptyList(),
    val savedAedThisWeek: Double = 0.0,
    val savedGramsThisWeek: Int = 0,
    val rescuedCountThisWeek: Int = 0,
    val wastedCountThisWeek: Int = 0,
    val isScanning: Boolean = false,
    val scanMerchant: String = "",
    val scanSafetyNote: String = "AI expiry dates are estimates (est.) — always smell, inspect, and check before eating.",
    val scanIsDemoFallback: Boolean = false,
    val editableScannedItems: List<EditableScannedItem> = emptyList(),
    val isLoadingRecipes: Boolean = false,
    val rescueRecipes: List<RescueRecipeDto> = emptyList(),
    val recipesIsDemoFallback: Boolean = false,
    val errorMessage: String? = null,
)

private data class TransientState(
    val isScanning: Boolean = false,
    val scanMerchant: String = "",
    val scanSafetyNote: String = "AI expiry dates are estimates (est.) — always smell, inspect, and check before eating.",
    val scanIsDemoFallback: Boolean = false,
    val editableScannedItems: List<EditableScannedItem> = emptyList(),
    val isLoadingRecipes: Boolean = false,
    val rescueRecipes: List<RescueRecipeDto> = emptyList(),
    val recipesIsDemoFallback: Boolean = false,
    val errorMessage: String? = null,
)

class FridgeViewModel(
    private val repository: FridgeRepository,
) : ViewModel() {

    private val transient = MutableStateFlow(TransientState())

    val uiState: StateFlow<FridgeUiState> = combine(
        repository.observePetProfile(),
        repository.observeActiveItems(),
        repository.observeCurrentWeekEvents(),
        repository.observeAllEvents(),
        transient,
    ) { profileOrNull, activeItems, weeklyEvents, allEvents, transientState ->
        val profile = profileOrNull ?: PetProfileEntity()
        val species = PetSpecies.fromId(profile.speciesId)
        val mood = repository.computeCurrentMood(profile, activeItems)
        val attention = activeItems.filter {
            PetMoodEngine.daysUntilExpiry(it.expiryEpochDay) <= 2
        }
        val rescuedEvents = weeklyEvents.filter { it.rescued }
        val wastedEvents = weeklyEvents.filter { !it.rescued }

        FridgeUiState(
            isLoaded = true,
            petProfile = profile,
            species = species,
            mood = mood,
            activeItems = activeItems,
            needsAttentionItems = attention,
            weeklyEvents = weeklyEvents,
            allEvents = allEvents,
            savedAedThisWeek = rescuedEvents.sumOf { it.priceAed },
            savedGramsThisWeek = rescuedEvents.sumOf { it.estimatedGrams },
            rescuedCountThisWeek = rescuedEvents.size,
            wastedCountThisWeek = wastedEvents.size,
            isScanning = transientState.isScanning,
            scanMerchant = transientState.scanMerchant,
            scanSafetyNote = transientState.scanSafetyNote,
            scanIsDemoFallback = transientState.scanIsDemoFallback,
            editableScannedItems = transientState.editableScannedItems,
            isLoadingRecipes = transientState.isLoadingRecipes,
            rescueRecipes = transientState.rescueRecipes,
            recipesIsDemoFallback = transientState.recipesIsDemoFallback,
            errorMessage = transientState.errorMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FridgeUiState(),
    )

    fun completeOnboarding(
        species: PetSpecies,
        customName: String,
        notificationHour: Int,
        seedDemoGroceries: Boolean,
    ) {
        viewModelScope.launch {
            repository.completeOnboarding(species, customName, notificationHour, seedDemoGroceries)
        }
    }

    fun pamperPet() {
        viewModelScope.launch {
            repository.pamperPetCheer()
        }
    }

    fun analyzeImage(
        imageBase64: String,
        mimeType: String,
        scanMode: String,
        onDone: () -> Unit,
    ) {
        viewModelScope.launch {
            transient.update { it.copy(isScanning = true, errorMessage = null) }
            runCatching {
                repository.scanReceiptOrFridge(imageBase64, mimeType, scanMode)
            }.onSuccess { response ->
                val editable = response.items.map { dto ->
                    EditableScannedItem(
                        localId = UUID.randomUUID().toString(),
                        cleanName = dto.cleanName,
                        rawLabel = dto.rawLabel,
                        quantityLabel = dto.quantityLabel,
                        shelf = runCatching { ShelfType.valueOf(dto.shelf.uppercase()) }
                            .getOrDefault(ShelfType.FRIDGE),
                        shelfLifeDays = dto.shelfLifeDays.coerceIn(1, 365),
                        priceAed = dto.priceAed,
                        storageTip = dto.storageTip,
                        selected = true,
                    )
                }
                transient.update {
                    it.copy(
                        isScanning = false,
                        scanMerchant = response.merchantName,
                        scanSafetyNote = response.safetyNote,
                        scanIsDemoFallback = response.isDemoFallback,
                        editableScannedItems = editable,
                    )
                }
                onDone()
            }.onFailure { err ->
                transient.update {
                    it.copy(
                        isScanning = false,
                        errorMessage = err.message ?: "Could not analyze image.",
                    )
                }
            }
        }
    }

    fun runSampleUaeScan(scanMode: String, onDone: () -> Unit) {
        analyzeImage(
            imageBase64 = "U0FNUExFX1VBRV9SRUNFSVBU",
            mimeType = "image/jpeg",
            scanMode = scanMode,
            onDone = onDone,
        )
    }

    fun updateScannedItem(
        localId: String,
        transform: (EditableScannedItem) -> EditableScannedItem,
    ) {
        transient.update { state ->
            state.copy(
                editableScannedItems = state.editableScannedItems.map {
                    if (it.localId == localId) transform(it) else it
                }
            )
        }
    }

    fun addBlankScannedItem() {
        val blank = EditableScannedItem(
            localId = UUID.randomUUID().toString(),
            cleanName = "",
            rawLabel = "Manual entry",
            quantityLabel = "1 pc",
            shelf = ShelfType.FRIDGE,
            shelfLifeDays = 4,
            priceAed = 10.0,
            storageTip = "Keep sealed and check freshness before eating.",
            selected = true,
        )
        transient.update { it.copy(editableScannedItems = it.editableScannedItems + blank) }
    }

    fun removeScannedItem(localId: String) {
        transient.update { state ->
            state.copy(
                editableScannedItems = state.editableScannedItems.filterNot { it.localId == localId }
            )
        }
    }

    fun saveReviewedItems(onSaved: () -> Unit) {
        val items = transient.value.editableScannedItems
        viewModelScope.launch {
            repository.saveReviewedItems(items)
            transient.update { it.copy(editableScannedItems = emptyList()) }
            onSaved()
        }
    }

    fun markItemUsed(item: FoodItemEntity) {
        viewModelScope.launch { repository.markItemUsed(item) }
    }

    fun moveToFreezer(item: FoodItemEntity) {
        viewModelScope.launch { repository.moveToFreezer(item) }
    }

    fun markItemWasted(item: FoodItemEntity) {
        viewModelScope.launch { repository.markItemWasted(item) }
    }

    fun loadRescueRecipes() {
        val snapshot = uiState.value
        val urgent = snapshot.needsAttentionItems.ifEmpty { snapshot.activeItems.take(4) }
        if (urgent.isEmpty()) {
            transient.update { it.copy(rescueRecipes = emptyList()) }
            return
        }
        viewModelScope.launch {
            transient.update { it.copy(isLoadingRecipes = true, errorMessage = null) }
            runCatching {
                repository.suggestRescueRecipes(urgent)
            }.onSuccess { response ->
                transient.update {
                    it.copy(
                        isLoadingRecipes = false,
                        rescueRecipes = response.recipes,
                        recipesIsDemoFallback = response.isDemoFallback,
                    )
                }
            }.onFailure { err ->
                transient.update {
                    it.copy(
                        isLoadingRecipes = false,
                        errorMessage = err.message ?: "Could not load rescue recipes.",
                    )
                }
            }
        }
    }

    fun markRecipeCooked(recipe: RescueRecipeDto, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.markRecipeCooked(recipe.usedIngredients)
            onDone()
        }
    }

    fun celebrateZeroWasteWeek() {
        viewModelScope.launch {
            repository.recordZeroWasteWeekCelebration()
        }
    }

    fun refreshPetDialogue() {
        viewModelScope.launch {
            repository.refreshPetDialogue()
        }
    }

    fun clearError() {
        transient.update { it.copy(errorMessage = null) }
    }

    class Factory(private val repository: FridgeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FridgeViewModel(repository) as T
        }
    }
}
