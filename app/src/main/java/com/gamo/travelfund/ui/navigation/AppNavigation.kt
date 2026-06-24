package com.gamo.travelfund.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gamo.travelfund.TravelFundApp
import com.gamo.travelfund.data.model.entity.BudgetCategoryEntity
import com.gamo.travelfund.data.model.entity.SavingMovementEntity
import com.gamo.travelfund.data.repository.BudgetCategoryRepository
import com.gamo.travelfund.data.repository.CurrencyRepository
import com.gamo.travelfund.data.repository.SavingMovementRepository
import com.gamo.travelfund.data.repository.TripRepository
import com.gamo.travelfund.ui.views.modelFactory.BudgetCategoryViewModelFactory
import com.gamo.travelfund.ui.views.modelFactory.CurrencyViewModelFactory
import com.gamo.travelfund.ui.views.modelFactory.SavingMovementViewModelFactory
import com.gamo.travelfund.ui.views.modelFactory.TripViewModelFactory
import com.gamo.travelfund.ui.views.screens.AddTripScreen
import com.gamo.travelfund.ui.views.screens.HomeScreen
import com.gamo.travelfund.ui.views.screens.TripDetailScreen
import com.gamo.travelfund.ui.views.viewmodel.BudgetCategoryViewModel
import com.gamo.travelfund.ui.views.viewmodel.CurrencyViewModel
import com.gamo.travelfund.ui.views.viewmodel.SavingMovementViewModel
import com.gamo.travelfund.ui.views.viewmodel.TripViewModel
import kotlin.collections.emptyList

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

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,

        enterTransition = { EnterTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                trips = tripsWithStats,
                onAddTrip = {
                    navController.navigate(Screen.AddTrip.route)
                },
                onTripClick = { trip ->
                    navController.navigate(Screen.TripDetail.createRoute(trip.id))
                }
            )
        }
        composable(Screen.AddTrip.route) {
            AddTripScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSaveTrip = { trip ->
                    tripViewModel.insertTrip(trip)
                    navController.popBackStack()
                }
            )
        }
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
                    kotlinx.coroutines.flow.flowOf<List<SavingMovementEntity>>(emptyList())
                }
            }.collectAsState(initial = emptyList())

            val categories by remember(tripId) {
                if (tripId != null) {
                    budgetCategoryViewModel.getCategoriesForTrip(tripId)
                } else {
                    kotlinx.coroutines.flow.flowOf<List<BudgetCategoryEntity>>(emptyList())
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
                    savingViewModel.insertMovement(movement)
                },
                onSaveCategory = { category ->
                    budgetCategoryViewModel.insertCategory(category)
                }
            )
        }
    }
}
