package com.fridgepet.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fridgepet.app.domain.EvolutionStage
import com.fridgepet.app.domain.PetState
import com.fridgepet.app.ui.components.FridgePetAvatar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: (petName: String, seedDemoGroceries: Boolean) -> Unit,
) {
    var petName by rememberSaveable { mutableStateOf("Brrbie") }
    var seedDemoGroceries by rememberSaveable { mutableStateOf(true) }
    val presetNames = listOf("Brrbie", "Chilli", "Zaatar", "Laban", "Mochi")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                text = "Meet your Fridge Pet",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "A tiny creature that lives in your fridge, tracks expiry dates from receipts, and celebrates when you rescue food instead of tossing dirhams.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            FridgePetAvatar(
                petState = PetState.CONTENT,
                evolutionStage = EvolutionStage.STAGE_1,
                size = 200.dp,
            )

            OutlinedTextField(
                value = petName,
                onValueChange = { if (it.length <= 24) petName = it },
                label = { Text("Name your pet") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                presetNames.forEach { preset ->
                    FilterChip(
                        selected = petName.equals(preset, ignoreCase = true),
                        onClick = { petName = preset },
                        label = { Text(preset) },
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(),
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
                            text = "Pre-loads 4 realistic fridge items so you can test moods & rescue recipes right away.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { onFinishOnboarding(petName, seedDemoGroceries) },
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                Text(
                    text = "Hatch ${petName.ifBlank { "My Pet" }}",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}
