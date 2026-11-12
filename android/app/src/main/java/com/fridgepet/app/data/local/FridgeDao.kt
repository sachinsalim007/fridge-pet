package com.fridgepet.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FridgeDao {

    @Query("SELECT * FROM food_items WHERE status = 'ACTIVE' ORDER BY expiryEpochDay ASC, id ASC")
    fun observeActiveItems(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE status = 'ACTIVE' ORDER BY expiryEpochDay ASC, id ASC")
    suspend fun getActiveItemsOnce(): List<FoodItemEntity>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getItemById(id: Long): FoodItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<FoodItemEntity>)

    @Update
    suspend fun updateItem(item: FoodItemEntity)

    @Query("DELETE FROM food_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("SELECT * FROM pet_profile WHERE id = 1")
    fun observePetProfile(): Flow<PetProfileEntity?>

    @Query("SELECT * FROM pet_profile WHERE id = 1")
    suspend fun getPetProfileOnce(): PetProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPetProfile(profile: PetProfileEntity)

    @Insert
    suspend fun insertWasteEvent(event: WasteEventEntity)

    @Query("SELECT * FROM waste_events WHERE weekStartEpochDay = :weekStartEpochDay ORDER BY timestampEpochMs DESC")
    fun observeWeeklyEvents(weekStartEpochDay: Long): Flow<List<WasteEventEntity>>

    @Query("SELECT * FROM waste_events ORDER BY timestampEpochMs DESC")
    fun observeAllEvents(): Flow<List<WasteEventEntity>>
}
