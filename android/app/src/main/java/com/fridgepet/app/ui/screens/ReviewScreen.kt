package com.fridgepet.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fridgepet.app.data.repository.EditableScannedItem
import com.fridgepet.app.domain.ShelfType
import com.fridgepet.app.ui.components.FoodSafetyBanner
import com.fridgepet.app.ui.viewmodel.FridgeUiState

@Composable
fun ReviewScreen(
    state: FridgeUiState,
    onUpdateItem: (String, (EditableScannedItem) -> EditableScannedItem) -> Unit,
    onAddBlankItem: () -> Unit,
    onRemoveItem: (String) -> Unit,
    onSaveItems: () -> Unit,
    onBackToScan: () -> Unit,
) {
    val selectedCount = state.editableScannedItems.count { it.selected && it.cleanName.isNotBlank() }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Review Extracted Items",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    if (state.scanMerchant.isNotBlank()) {
                        Text(
                            text = "Source: ${state.scanMerchant}" +
                                if (state.scanIsDemoFallback) " · Offline Demo" else " · Gemini Vision",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    FoodSafetyBanner(text = state.scanSafetyNote)
                }
            }

            items(state.editableScannedItems, key = { it.localId }) { item ->
                EditableScannedItemCard(
                    item = item,
                    onUpdate = { transform -> onUpdateItem(item.localId, transform) },
                    onRemove = { onRemoveItem(item.localId) },
                )
            }

            item {
                OutlinedButton(
                    onClick = onAddBlankItem,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Add Missing Item")
                }
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = onBackToScan,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(50.dp),
                ) {
                    Text("Rescan")
                }
                Button(
                    enabled = selectedCount > 0,
                    onClick = onSaveItems,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                ) {
                    Text("Save $selectedCount Item(s) to Pantry")
                }
            }
        }
    }
}

@Composable
private fun EditableScannedItemCard(
    item: EditableScannedItem,
    onUpdate: ((EditableScannedItem) -> EditableScannedItem) -> Unit,
    onRemove: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Checkbox(
                    checked = item.selected,
                    onCheckedChange = { checked -> onUpdate { it.copy(selected = checked) } },
                )
                Column(modifier = Modifier.weight(1f)) {
                    if (item.rawLabel.isNotBlank()) {
                        Text(
                            text = "Receipt: ${item.rawLabel}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Remove item",
                    )
                }
            }

            OutlinedTextField(
                value = item.cleanName,
                onValueChange = { value -> onUpdate { it.copy(cleanName = value) } },
                label = { Text("Clean food name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = item.quantityLabel,
                    onValueChange = { value -> onUpdate { it.copy(quantityLabel = value) } },
                    label = { Text("Qty") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = if (item.priceAed == 0.0) "" else item.priceAed.toString(),
                    onValueChange = { raw ->
                        val parsed = raw.toDoubleOrNull() ?: 0.0
                        onUpdate { it.copy(priceAed = parsed) }
                    },
                    label = { Text("Price (AED)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ShelfType.entries.forEach { shelf ->
                    FilterChip(
                        selected = item.shelf == shelf,
                        onClick = { onUpdate { it.copy(shelf = shelf) } },
                        label = { Text(shelf.displayName) },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Estimated shelf life: ${item.shelfLifeDays} day(s)",
                    style = MaterialTheme.typography.titleMedium,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            onUpdate { it.copy(shelfLifeDays = (it.shelfLifeDays - 1).coerceAtLeast(1)) }
                        },
                    ) {
                        Icon(Icons.Rounded.Remove, contentDescription = "Decrease days")
                    }
                    IconButton(
                        onClick = {
                            onUpdate { it.copy(shelfLifeDays = (it.shelfLifeDays + 1).coerceAtMost(365)) }
                        },
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Increase days")
                    }
                }
            }

            if (item.storageTip.isNotBlank()) {
                Text(
                    text = "Tip: ${item.storageTip}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
