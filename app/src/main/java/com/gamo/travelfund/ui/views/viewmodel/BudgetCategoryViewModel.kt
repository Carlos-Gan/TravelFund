package com.gamo.travelfund.ui.views.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamo.travelfund.data.model.entity.BudgetCategoryEntity
import com.gamo.travelfund.data.repository.BudgetCategoryRepository
import kotlinx.coroutines.launch

class BudgetCategoryViewModel(
    private val repository: BudgetCategoryRepository
) : ViewModel() {
    fun getCategoriesForTrip(tripId: Long) =
        repository.getCategoriesForTrip(tripId)

    fun insertCategory(category: BudgetCategoryEntity) {
        viewModelScope.launch {
            repository.insertCategory(category)
        }
    }

    fun updateCategory(category: BudgetCategoryEntity) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: BudgetCategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }
}