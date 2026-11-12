package com.fridgepet.app.data.local

import androidx.room.TypeConverter
import com.fridgepet.app.domain.ShelfType

class Converters {
    @TypeConverter
    fun fromShelfType(value: ShelfType): String = value.name

    @TypeConverter
    fun toShelfType(value: String): ShelfType =
        runCatching { ShelfType.valueOf(value) }.getOrDefault(ShelfType.FRIDGE)

    @TypeConverter
    fun fromItemStatus(value: ItemStatus): String = value.name

    @TypeConverter
    fun toItemStatus(value: String): ItemStatus =
        runCatching { ItemStatus.valueOf(value) }.getOrDefault(ItemStatus.ACTIVE)
}
