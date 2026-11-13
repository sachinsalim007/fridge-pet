package com.fridgepet.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Sparkles
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fridgepet.app.domain.EvolutionStage
import com.fridgepet.app.domain.PetSpecies
import com.fridgepet.app.domain.PetState
import com.fridgepet.app.ui.components.FridgePetAvatar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: (
        species: PetSpecies,
        customName: String,
        notificationHour: Int,
        seedDemoGroceries: Boolean,
    ) -> Unit,
) {
    var selectedSpecies by remember { mutableStateOf(PetSpecies.MOCHI) }
    var petName by rememberSaveable { mutableStateOf("Mochi") }
    var notificationHour by rememberSaveable { mutableIntStateOf(9) } // 9:00 AM default
    var seedDemoGroceries by rememberSaveable { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "Choose Your Fridge Pet",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Pick a creature to watch over your pantry. Rub or tap your buddy anytime to pamper them!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            // Interactive Live Creature Preview
            FridgePetAvatar(
                petState = PetState.CONTENT,
                evolutionStage = EvolutionStage.STAGE_1,
                species = selectedSpecies,
                size = 188.dp,
                interactive = true,
            )

            Text(
                text = "${selectedSpecies.title} · ${selectedSpecies.foodAffinity}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            // 10-Pet Selection Carousel
            Text(
                text = "Swipe & Select Species (10 Pets)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(PetSpecies.entries) { species ->
                    val isSelected = selectedSpecies == species
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .width(130.dp)
                            .clickable {
                                selectedSpecies = species
                                petName = species.displayName
                            },
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            FridgePetAvatar(
                                petState = PetState.CONTENT,
                                evolutionStage = EvolutionStage.STAGE_1,
                                species = species,
                                size = 72.dp,
                                interactive = false,
                            )
                            Text(
                                text = species.displayName,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = species.foodAffinity,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }

            // Custom Pet Name Input
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = petName,
                    onValueChange = { if (it.length <= 24) petName = it },
                    label = { Text("Custom Pet Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    listOf(selectedSpecies.displayName, "Habibi", "Zuzu", "Pickles", "Chef ${selectedSpecies.displayName}").forEach { suggestion ->
                        FilterChip(
                            selected = petName.equals(suggestion, ignoreCase = true),
                            onClick = { petName = suggestion },
                            label = { Text(suggestion) },
                        )
                    }
                }
            }

            // Notification Hour Picker
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Daily Expiry Alert Time: ${formatHour(notificationHour)}",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    Text(
                        text = "When $petName checks your shelves each morning to nudge you about urgent items.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(8, 9, 10, 18).forEach { hour ->
                            FilterChip(
                                selected = notificationHour == hour,
                                onClick = { notificationHour = hour },
                                label = { Text(formatHour(hour)) },
                            )
                        }
                    }
                }
            }

            // Seed sample UAE groceries toggle
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = seedDemoGroceries,
                        onCheckedChange = { seedDemoGroceries = it },
                    )
                    Column(modifier = Modifier.padding(start = 6.dp)) {
                        Text(
                            text = "Start with sample UAE groceries",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Pre-loads chicken, spinach, milk & flatbread so you can test Mochi right away.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Button(
                onClick = {
                    onFinishOnboarding(selectedSpecies, petName, notificationHour, seedDemoGroceries)
                },
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(54.dp),
            ) {
                Text(
                    text = "Hatch ${petName.ifBlank { selectedSpecies.displayName }}",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

private fun formatHour(hour: Int): String = when {
    hour == 0 -> "12:00 AM"
    hour < 12 -> "$hour:00 AM"
    hour == 12 -> "12:00 PM"
    else -> "${hour - 12}:00 PM"
}
