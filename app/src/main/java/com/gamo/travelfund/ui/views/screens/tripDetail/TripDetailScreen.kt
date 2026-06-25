package com.gamo.travelfund.ui.views.screens.tripDetail

import androidx.compose.runtime.Composable
import com.gamo.travelfund.data.model.entity.BudgetCategoryEntity
import com.gamo.travelfund.data.model.entity.SavingMovementEntity
import com.gamo.travelfund.data.model.entity.TripEntity
import com.gamo.travelfund.data.stats.BudgetCategoryWithStats

@Composable
fun TripDetailScreen(
    trip: TripEntity?,
    movements: List<SavingMovementEntity>,
    categories: List<BudgetCategoryWithStats>,
    exchangeRate: Double,
    onBack: () -> Unit,
    onAiClick: () -> Unit,
    onSaveMovement: (SavingMovementEntity) -> Unit,
    onUpdateMovement: (SavingMovementEntity) -> Unit,
    onDeleteMovement: (SavingMovementEntity) -> Unit,
    onSaveCategory: (BudgetCategoryEntity) -> Unit,
    onUpdateCategory: (BudgetCategoryEntity) -> Unit,
    onDeleteCategory: (BudgetCategoryEntity) -> Unit
) {
    TripDetailContent(
        trip = trip,
        movements = movements,
        categories = categories,
        exchangeRate = exchangeRate,
        onBack = onBack,
        onSaveMovement = onSaveMovement,
        onUpdateMovement = onUpdateMovement,
        onDeleteMovement = onDeleteMovement,
        onSaveCategory = onSaveCategory,
        onUpdateCategory = onUpdateCategory,
        onDeleteCategory = onDeleteCategory,
        baseCurrency = trip?.baseCurrency ?: "",
        onAiClick = onAiClick
    )
}