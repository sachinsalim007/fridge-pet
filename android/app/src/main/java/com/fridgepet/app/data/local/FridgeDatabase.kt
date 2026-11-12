package com.fridgepet.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [FoodItemEntity::class, PetProfileEntity::class, WasteEventEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class FridgeDatabase : RoomDatabase() {
    abstract fun fridgeDao(): FridgeDao

    companion object {
        @Volatile
        private var instance: FridgeDatabase? = null

        fun getInstance(context: Context): FridgeDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FridgeDatabase::class.java,
                    "fridge_pet.db"
                ).build().also { instance = it }
            }
        }
    }
}
