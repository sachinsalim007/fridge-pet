package com.fridgepet.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fridgepet.app.ui.navigation.FridgeNavHost
import com.fridgepet.app.ui.theme.FridgePetTheme
import com.fridgepet.app.ui.viewmodel.FridgeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as FridgePetApp
        val factory = FridgeViewModel.Factory(app.repository)

        setContent {
            FridgePetTheme {
                val vm: FridgeViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsStateWithLifecycle()

                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                ) { _ -> }

                LaunchedEffect(state.petProfile.onboarded) {
                    if (
                        state.petProfile.onboarded &&
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS,
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                FridgeNavHost(state = state, viewModel = vm)
            }
        }
    }
}
