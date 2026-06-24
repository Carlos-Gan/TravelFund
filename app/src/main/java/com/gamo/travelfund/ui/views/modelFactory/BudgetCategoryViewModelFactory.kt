package com.gamo.travelfund.ui.views.modelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gamo.travelfund.data.repository.BudgetCategoryRepository
import com.gamo.travelfund.ui.views.viewmodel.BudgetCategoryViewModel

class BudgetCategoryViewModelFactory(
    private val repository: BudgetCategoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetCategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetCategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}