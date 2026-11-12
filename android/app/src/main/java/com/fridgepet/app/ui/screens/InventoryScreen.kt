package com.fridgepet.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fridgepet.app.data.local.FoodItemEntity
import com.fridgepet.app.domain.PetMoodEngine
import com.fridgepet.app.domain.ShelfType
import com.fridgepet.app.ui.components.ExpiryChip
import com.fridgepet.app.ui.components.FoodSafetyBanner
import com.fridgepet.app.ui.viewmodel.FridgeUiState

@Composable
fun InventoryScreen(
    state: FridgeUiState,
    onMarkUsed: (FoodItemEntity) -> Unit,
    onMoveToFreezer: (FoodItemEntity) -> Unit,
    onMarkWasted: (FoodItemEntity) -> Unit,
) {
    var selectedShelf by remember { mutableStateOf<ShelfType?>(null) }

    val grouped = remember(state.activeItems, selectedShelf) {
        val filtered = if (selectedShelf == null) {
            state.activeItems
        } else {
            state.activeItems.filter { it.shelf == selectedShelf }
        }
        ShelfType.entries.mapNotNull { shelf ->
            val items = filtered.filter { it.shelf == shelf }
            if (items.isEmpty()) null else shelf to items
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Pantry & Fridge",
                    style = MaterialTheme.typography.headlineMedium,
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedShelf == null,
                            onClick = { selectedShelf = null },
                            label = { Text("All (${state.activeItems.size})") },
                        )
                    }
                    items(ShelfType.entries) { shelf ->
                        val count = state.activeItems.count { it.shelf == shelf }
                        FilterChip(
                            selected = selectedShelf == shelf,
                            onClick = { selectedShelf = shelf },
                            label = { Text("${shelf.displayName} ($count)") },
                        )
                    }
                }
            }
        }

        if (grouped.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "This shelf is empty",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Scan a receipt or fridge photo to track expiry dates and keep ${state.petProfile.name} well-fed.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        } else {
            grouped.forEach { (shelf, shelfItems) ->
                item(key = "header_${shelf.name}") {
                    Text(
                        text = "${shelf.displayName} (${shelfItems.size})",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                items(shelfItems, key = { it.id }) { item ->
                    InventoryItemCard(
                        item = item,
                        onMarkUsed = { onMarkUsed(item) },
                        onMoveToFreezer = { onMoveToFreezer(item) },
                        onMarkWasted = { onMarkWasted(item) },
                    )
                }
            }
        }

        item {
            FoodSafetyBanner()
        }
    }
}

@Composable
private fun InventoryItemCard(
    item: FoodItemEntity,
    onMarkUsed: () -> Unit,
    onMoveToFreezer: () -> Unit,
    onMarkWasted: () -> Unit,
) {
    val days = PetMoodEngine.daysUntilExpiry(item.expiryEpochDay)
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "${item.quantityLabel} · AED ${"%.2f".format(item.priceAed)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (item.rawLabel.isNotBlank() && !item.rawLabel.equals(item.name, ignoreCase = true)) {
                        Text(
                            text = item.rawLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                ExpiryChip(daysUntilExpiry = days)
            }

            if (item.storageTip.isNotBlank()) {
                Text(
                    text = item.storageTip,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = onMarkUsed,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Used")
                }

                if (item.shelf != ShelfType.FREEZER) {
                    OutlinedButton(
                        onClick = onMoveToFreezer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Rounded.AcUnit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Freeze")
                    }
                }

                IconButton(onClick = onMarkWasted) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Mark tossed/wasted",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}
