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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fridgepet.app.domain.EvolutionStage
import com.fridgepet.app.ui.components.FridgePetAvatar
import com.fridgepet.app.ui.viewmodel.FridgeUiState

@Composable
fun WeeklyReportScreen(
    state: FridgeUiState,
    onCelebrateGoodWeek: () -> Unit,
) {
    val kgSaved = state.savedGramsThisWeek / 1000.0
    val totalEvents = state.rescuedCountThisWeek + state.wastedCountThisWeek
    val rescueRatio = if (totalEvents == 0) 1f else state.rescuedCountThisWeek.toFloat() / totalEvents.toFloat()

    val nextStage = when (state.mood.evolutionStage) {
        EvolutionStage.STAGE_1 -> EvolutionStage.STAGE_2
        EvolutionStage.STAGE_2 -> EvolutionStage.STAGE_3
        EvolutionStage.STAGE_3 -> null
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text(
                text = "Weekly Impact Report",
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricHeroCard(
                    title = "Money Saved",
                    value = "AED ${"%.0f".format(state.savedAedThisWeek)}",
                    subtitle = "${state.rescuedCountThisWeek} item(s) rescued",
                    modifier = Modifier.weight(1f),
                )
                MetricHeroCard(
                    title = "Food Saved",
                    value = "${"%.2f".format(kgSaved)} kg",
                    subtitle = "${state.wastedCountThisWeek} item(s) tossed",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Zero-Waste Score", style = MaterialTheme.typography.titleMedium)
                        Text("${(rescueRatio * 100).toInt()}%", style = MaterialTheme.typography.titleMedium)
                    }
                    LinearProgressIndicator(
                        progress = { rescueRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                    )
                    Text(
                        text = "Every rescued item keeps AED in your wallet and keeps ${state.petProfile.name} happy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        FridgePetAvatar(
                            petState = state.mood.state,
                            evolutionStage = state.mood.evolutionStage,
                            size = 92.dp,
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Evolution · ${state.mood.evolutionStage.title}",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = state.mood.evolutionStage.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Low-waste streak: ${state.petProfile.consecutiveGoodWeeks} week(s)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    if (nextStage != null) {
                        val progress = (
                            state.petProfile.consecutiveGoodWeeks.toFloat() /
                                nextStage.requiredGoodWeeks.toFloat()
                            ).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                        )
                        Text(
                            text = "Next unlock: ${nextStage.title} at ${nextStage.requiredGoodWeeks} consecutive good weeks",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Button(
                        onClick = onCelebrateGoodWeek,
                        enabled = state.wastedCountThisWeek == 0,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Rounded.EmojiEvents, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            if (state.wastedCountThisWeek == 0) {
                                "Complete Zero-Waste Week (+1 Streak)"
                            } else {
                                "Zero-Waste Week Paused (Item Wasted)"
                            }
                        )
                    }
                }
            }
        }

        if (state.weeklyEvents.isNotEmpty()) {
            item {
                Text(
                    text = "This Week's Activity",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            items(state.weeklyEvents.take(15), key = { it.id }) { event ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = event.itemName.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = if (event.rescued) "Rescued & eaten" else "Expired / tossed",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (event.rescued) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                            )
                        }
                        Text(
                            text = "${if (event.rescued) "+" else "-"}AED ${"%.2f".format(event.priceAed)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (event.rescued) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricHeroCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
