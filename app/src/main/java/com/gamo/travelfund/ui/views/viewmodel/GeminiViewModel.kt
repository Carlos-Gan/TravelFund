package com.gamo.travelfund.ui.views.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamo.travelfund.data.remote.GeminiRecommendationResponse
import com.gamo.travelfund.data.repository.GeminiRepository
import com.gamo.travelfund.data.stats.BudgetCategoryWithStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GeminiViewModel(
    private val repository: GeminiRepository
) : ViewModel() {

    private val _recommendation = MutableStateFlow<GeminiRecommendationResponse?>(null)
    val recommendation: StateFlow<GeminiRecommendationResponse?> = _recommendation

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun generateRecommendation(
        destination: String,
        totalBudget: Double,
        baseCurrency: String,
        destinationCurrency: String,
        categories: List<BudgetCategoryWithStats>,
        interests: String
    ) {
        viewModelScope.launch {
            _loading.value = true

            _recommendation.value = repository.generateTravelRecommendation(
                destination = destination,
                totalBudget = totalBudget,
                baseCurrency = baseCurrency,
                destinationCurrency = destinationCurrency,
                categories = categories,
                interests = interests
            )

            _loading.value = false
        }
    }

    fun clearRecommendation() {
        _recommendation.value = null
    }
}