package com.gamo.travelfund.data.remote

data class GeminiRecommendationResponse(
    val summary: String = "",
    val recommendedPlaces: List<RecommendedPlace> = emptyList(),
    val budgetTips: List<String> = emptyList(),
    val suggestedCategories: List<SuggestedCategory> = emptyList(),
    val budgetAnalysis: List<BudgetAnalysis> = emptyList()
)

data class RecommendedPlace(
    val name: String = "",
    val reason: String = "",
    val estimatedCost: String = ""
)

data class SuggestedCategory(
    val name: String = "",
    val emoji: String = "",
    val reason: String = ""
)

data class BudgetAnalysis(
    val category: String = "",
    val comment: String = ""
)