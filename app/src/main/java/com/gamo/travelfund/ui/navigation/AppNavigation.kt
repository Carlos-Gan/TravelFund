package com.gamo.travelfund.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gamo.travelfund.TravelFundApp
import com.gamo.travelfund.data.model.entity.BudgetCategoryEntity
import com.gamo.travelfund.data.model.entity.MovementType
import com.gamo.travelfund.data.model.entity.SavingMovementEntity
import com.gamo.travelfund.data.preferences.NotificationSettings
import com.gamo.travelfund.data.preferences.SettingsPreferences
import com.gamo.travelfund.data.repository.BudgetCategoryRepository
import com.gamo.travelfund.data.repository.CurrencyRepository
import com.gamo.travelfund.data.repository.GeminiRepository
import com.gamo.travelfund.data.repository.SavingMovementRepository
import com.gamo.travelfund.data.repository.TripRepository
import com.gamo.travelfund.services.NotificationHelper
import com.gamo.travelfund.ui.components.TravelFundNavigationBar
import com.gamo.travelfund.ui.views.modelFactory.BudgetCategoryViewModelFactory
import com.gamo.travelfund.ui.views.modelFactory.CurrencyViewModelFactory
import com.gamo.travelfund.ui.views.modelFactory.GeminiViewModelFactory
import com.gamo.travelfund.ui.views.modelFactory.SavingMovementViewModelFactory
import com.gamo.travelfund.ui.views.modelFactory.SettingsViewModelFactory
import com.gamo.travelfund.ui.views.modelFactory.TripViewModelFactory
import com.gamo.travelfund.ui.views.screens.AIRecommendationsScreen
import com.gamo.travelfund.ui.views.screens.AddTripScreen
import com.gamo.travelfund.ui.views.screens.HomeScreen
import com.gamo.travelfund.ui.views.screens.SettingsScreen
import com.gamo.travelfund.ui.views.screens.StatisticsScreen
import com.gamo.travelfund.ui.views.screens.tripDetail.TripDetailScreen
import com.gamo.travelfund.ui.views.viewmodel.BudgetCategoryViewModel
import com.gamo.travelfund.ui.views.viewmodel.CurrencyViewModel
import com.gamo.travelfund.ui.views.viewmodel.GeminiViewModel
import com.gamo.travelfund.ui.views.viewmodel.SavingMovementViewModel
import com.gamo.travelfund.ui.views.viewmodel.SettingsViewModel
import com.gamo.travelfund.ui.views.viewmodel.TripViewModel
import com.gamo.travelfund.utils.getSavingMilestone
import kotlinx.coroutines.flow.flowOf
import kotlin.collections.emptyList

@SuppressLint("MissingPermission")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val app = context.applicationContext as TravelFundApp
    val repository = TripRepository(app.database.tripDao())

    val tripViewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(repository)
    )

    val tripsWithStats by tripViewModel.tripsWithStats.collectAsState(initial = emptyList())

    val savingRepository = SavingMovementRepository(
        dao = app.database.savingMovementDao()
    )

    val savingViewModel: SavingMovementViewModel = viewModel(
        factory = SavingMovementViewModelFactory(
            savingRepository = savingRepository
        )
    )

    val currencyRepository = CurrencyRepository()

    val currencyViewModel: CurrencyViewModel = viewModel(
        factory = CurrencyViewModelFactory(currencyRepository)
    )

    val budgetCategoryRepository = BudgetCategoryRepository(
        dao = app.database.budgetCategoryDao()
    )

    val budgetCategoryViewModel: BudgetCategoryViewModel = viewModel(
        factory = BudgetCategoryViewModelFactory(budgetCategoryRepository)
    )

    val settingsPreferences = SettingsPreferences(context)

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(settingsPreferences)
    )

    val settings by settingsViewModel.settings.collectAsState(
        initial = NotificationSettings()
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Statistics.route,
        Screen.Settings.route
    )

    val geminiRepository = GeminiRepository()

    val geminiViewModel: GeminiViewModel = viewModel(
        factory = GeminiViewModelFactory(geminiRepository)
    )

    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                TravelFundNavigationBar(
                    navController = navController,
                )
            }
        }
    )
    { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Screen.Home.route,
            enterTransition = { EnterTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
            exitTransition = { ExitTransition.None }
        ) {

            //Home Screen
            composable(Screen.Home.route) {
                HomeScreen(
                    trips = tripsWithStats,
                    onAddTrip = {
                        navController.navigate(Screen.AddTrip.route)
                    },
                    onTripClick = { trip ->
                        navController.navigate(Screen.TripDetail.createRoute(trip.id))
                    },
                    onDeleteTrip = { trip ->
                        tripViewModel.deleteTrip(trip)
                    },
                    onEditTrip = { trip ->
                        navController.navigate(Screen.EditTrip.createRoute(trip.id))
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }
            //Add Trip
            composable(Screen.AddTrip.route) {
                AddTripScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onSubmitTrip = { trip ->
                        tripViewModel.insertTrip(trip)
                        navController.popBackStack()
                    }
                )
            }

            //Trip Detail
            composable(Screen.TripDetail.route) { backStackEntry ->

                val tripId = backStackEntry.arguments
                    ?.getString("tripId")
                    ?.toLongOrNull()

                val trip = tripsWithStats
                    .find { it.trip.id == tripId }
                    ?.trip

                val exchangeRate by currencyViewModel.exchangeRate.collectAsState()

                LaunchedEffect(trip?.destinationCurrency) {
                    if (trip != null) {
                        currencyViewModel.fetchExchangeRate(
                            baseCurrency = trip.baseCurrency,
                            destinationCurrency = trip.destinationCurrency
                        )
                    }
                }

                val movements by remember(tripId) {
                    if (tripId != null) {
                        savingViewModel.getMovementsByTrip(tripId)
                    } else {
                        flowOf<List<SavingMovementEntity>>(emptyList())
                    }
                }.collectAsState(initial = emptyList())

                val categories by remember(tripId) {
                    if (tripId != null) {
                        budgetCategoryViewModel.getCategoriesWithStats(tripId)
                    } else {
                        flowOf(emptyList())
                    }
                }.collectAsState(initial = emptyList())

                TripDetailScreen(
                    trip = trip,
                    movements = movements,
                    categories = categories,
                    exchangeRate = exchangeRate,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSaveMovement = { movement ->
                        val previousSaved = movements
                            .filter { it.type == MovementType.INCOME }
                            .sumOf { it.amount } -
                                movements
                                    .filter { it.type == MovementType.EXPENSE }
                                    .sumOf { it.amount }
                        val previousPercent = if ((trip?.totalBudget ?: 0.0) > 0) {
                            ((previousSaved / trip!!.totalBudget) * 100).toInt()
                        } else 0
                        savingViewModel.insertMovement(
                            movement = movement,
                            onAfterInsert = {
                                val currentSaved =
                                    savingViewModel.getRealSavedAmount(movement.tripId)

                                val currentPercent = if ((trip?.totalBudget ?: 0.0) > 0) {
                                    ((currentSaved / trip!!.totalBudget) * 100).toInt()
                                } else 0

                                val milestone = getSavingMilestone(
                                    previousPercent = previousPercent,
                                    currentPercent = currentPercent
                                )
                                if (
                                    milestone != null &&
                                    settings.notificationsEnabled &&
                                    settings.notifySavingGoal
                                ) {
                                    NotificationHelper.showNotification(
                                        context = context,
                                        title = "Meta de ahorro alcanzada",
                                        message = "Has alcanzado el ${milestone}% de tu meta de ahorro"
                                    )
                                }

                            }
                        )
                    },
                    onSaveCategory = { category ->
                        budgetCategoryViewModel.insertCategory(category)
                    },
                    onDeleteMovement = { movement ->
                        savingViewModel.deleteMovement(movement)
                    },
                    onUpdateMovement = { movement ->
                        savingViewModel.updateMovement(movement)
                    },
                    onUpdateCategory = { category ->
                        budgetCategoryViewModel.updateCategory(category)
                    },
                    onDeleteCategory = { category ->
                        budgetCategoryViewModel.deleteCategory(category)
                    },
                    onAiClick = {
                        if (trip != null){
                            navController.navigate(
                                Screen.AiRecomendations.createRoute(trip.id)
                            )
                        }
                    }
                )
            }

            //Edit Trip
            composable(
                Screen.EditTrip.route,
                arguments = listOf(navArgument("tripId") { type = NavType.LongType })
            ) { backStackEntry ->

                val tripId = backStackEntry.arguments?.getLong("tripId")

                val trip = tripsWithStats.find { it.trip.id == tripId }?.trip

                AddTripScreen(
                    editingTrip = trip,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSubmitTrip = { updatedTrip ->
                        tripViewModel.updateTrip(updatedTrip)
                        navController.popBackStack()
                    }
                )
            }
            //Settings
            composable(Screen.Settings.route) {

                SettingsScreen(
                    settings = settings,
                    onNotificationsEnabledChange = settingsViewModel::setNotificationsEnabled,
                    onNotifyFewDaysChange = settingsViewModel::setNotifyFewDays,
                    onNotifySavingGoalChange = settingsViewModel::setNotifySavingGoal,
                    onNotifyNoSavingsChange = settingsViewModel::setNotifyNoSavings,
                    onNotifyExchangeRateChange = settingsViewModel::setNotifyExchangeRate
                )
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen(trips = tripsWithStats)
            }

            composable(
                route = Screen.AiRecomendations.route,
                arguments = listOf(
                    navArgument("tripId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->

                val tripId = backStackEntry.arguments?.getLong("tripId")

                val trip = tripsWithStats.find { it.trip.id == tripId }?.trip

                val categories by remember(tripId) {
                    if (tripId != null) {
                        budgetCategoryViewModel.getCategoriesWithStats(tripId)
                    } else {
                        flowOf(emptyList())
                    }
                }.collectAsState(initial = emptyList())

                val recommendation by geminiViewModel.recommendation.collectAsState()
                val loading by geminiViewModel.loading.collectAsState()

                AIRecommendationsScreen(
                    trip = trip,
                    categories = categories,
                    recommendation = recommendation,
                    loading = loading,
                    onGenerate = { interests ->
                        if (trip != null) {
                            geminiViewModel.generateRecommendation(
                                destination = trip.destination,
                                totalBudget = trip.totalBudget,
                                baseCurrency = trip.baseCurrency,
                                destinationCurrency = trip.destinationCurrency,
                                categories = categories,
                                interests = interests
                            )
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
