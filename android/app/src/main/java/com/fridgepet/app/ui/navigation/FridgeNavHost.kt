package com.fridgepet.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Kitchen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fridgepet.app.ui.screens.HomeScreen
import com.fridgepet.app.ui.screens.InventoryScreen
import com.fridgepet.app.ui.screens.OnboardingScreen
import com.fridgepet.app.ui.screens.RescueRecipeScreen
import com.fridgepet.app.ui.screens.ReviewScreen
import com.fridgepet.app.ui.screens.ScanScreen
import com.fridgepet.app.ui.screens.WeeklyReportScreen
import com.fridgepet.app.ui.viewmodel.FridgeUiState
import com.fridgepet.app.ui.viewmodel.FridgeViewModel

private object Routes {
    const val HOME = "home"
    const val SCAN = "scan"
    const val INVENTORY = "inventory"
    const val REPORT = "report"
    const val REVIEW = "review"
    const val RECIPES = "recipes"
}

private data class BottomNavTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun FridgeNavHost(
    state: FridgeUiState,
    viewModel: FridgeViewModel,
) {
    if (!state.isLoaded) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (!state.petProfile.onboarded) {
        OnboardingScreen(
            onFinishOnboarding = { petName, seedDemoGroceries ->
                viewModel.completeOnboarding(petName, seedDemoGroceries)
            },
        )
        return
    }

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearError()
    }

    val tabs = remember {
        listOf(
            BottomNavTab(Routes.HOME, "Home", Icons.Rounded.Home),
            BottomNavTab(Routes.SCAN, "Scan", Icons.Rounded.CameraAlt),
            BottomNavTab(Routes.INVENTORY, "Inventory", Icons.Rounded.Kitchen),
            BottomNavTab(Routes.REPORT, "Report", Icons.Rounded.Assessment),
        )
    }

    val showBottomBar = currentRoute in tabs.map { it.route }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    state = state,
                    onNavigateToScan = { navController.navigate(Routes.SCAN) },
                    onNavigateToRecipes = { navController.navigate(Routes.RECIPES) },
                    onMarkUsed = viewModel::markItemUsed,
                    onMoveToFreezer = viewModel::moveToFreezer,
                    onRefreshDialogue = viewModel::refreshPetDialogue,
                )
            }
            composable(Routes.SCAN) {
                ScanScreen(
                    state = state,
                    onAnalyzeImage = { base64, mimeType, scanMode ->
                        viewModel.analyzeImage(base64, mimeType, scanMode) {
                            navController.navigate(Routes.REVIEW)
                        }
                    },
                    onRunSampleScan = { scanMode ->
                        viewModel.runSampleUaeScan(scanMode) {
                            navController.navigate(Routes.REVIEW)
                        }
                    },
                )
            }
            composable(Routes.REVIEW) {
                ReviewScreen(
                    state = state,
                    onUpdateItem = viewModel::updateScannedItem,
                    onAddBlankItem = viewModel::addBlankScannedItem,
                    onRemoveItem = viewModel::removeScannedItem,
                    onSaveItems = {
                        viewModel.saveReviewedItems {
                            navController.navigate(Routes.INVENTORY) {
                                popUpTo(Routes.HOME)
                            }
                        }
                    },
                    onBackToScan = { navController.popBackStack() },
                )
            }
            composable(Routes.INVENTORY) {
                InventoryScreen(
                    state = state,
                    onMarkUsed = viewModel::markItemUsed,
                    onMoveToFreezer = viewModel::moveToFreezer,
                    onMarkWasted = viewModel::markItemWasted,
                )
            }
            composable(Routes.RECIPES) {
                RescueRecipeScreen(
                    state = state,
                    onLoadRecipes = viewModel::loadRescueRecipes,
                    onCookedRecipe = { recipe ->
                        viewModel.markRecipeCooked(recipe) {
                            navController.popBackStack()
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.REPORT) {
                WeeklyReportScreen(
                    state = state,
                    onCelebrateGoodWeek = viewModel::celebrateZeroWasteWeek,
                )
            }
        }
    }
}
